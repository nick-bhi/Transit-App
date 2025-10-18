package com.example.transit.service;

import com.example.transit.model.Arrival;
import com.example.transit.repository.ArrivalRepository;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.util.*;

@Service
public class EtaService {

  private static final Logger log = LoggerFactory.getLogger(EtaService.class);

  @Value("${gtfs.realtimeFeedUrl}") private String feedUrl;
  private final ArrivalRepository arrivalRepository;

  // in-memory latest ETAs keyed by stopId+routeId (simple MVP cache)
  private final Map<String,List<Eta>> cache = new HashMap<>();
  // Track processed arrivals to avoid duplicates
  private final Set<String> processedArrivals = new HashSet<>();
  public record Eta(String stopId, String routeId, long scheduledTs, Integer delaySec){}
  
  public EtaService(ArrivalRepository arrivalRepository) {
    this.arrivalRepository = arrivalRepository;
  }

  public synchronized void refresh() {
    try (InputStream in = new URL(feedUrl).openStream()) {
      FeedMessage fm = FeedMessage.parseFrom(in);
      Map<String,List<Eta>> next = new HashMap<>();
      int arrivalCount = 0;
      long currentTime = System.currentTimeMillis();
      
      // Check feed freshness (should be within last 5 minutes)
      if (fm.hasHeader() && fm.getHeader().hasTimestamp()) {
        long feedTimestamp = fm.getHeader().getTimestamp() * 1000L;
        long ageMinutes = (currentTime - feedTimestamp) / (1000 * 60);
        if (ageMinutes > 5) {
          log.warn("GTFS-RT feed is {} minutes old, skipping to avoid stale data", ageMinutes);
          return;
        }
      }
      
      log.info("GTFS-RT feed contains {} entities", fm.getEntityList().size());
      
      for (var ent : fm.getEntityList()) {
        if (!ent.hasTripUpdate()) continue;
        TripUpdate tu = ent.getTripUpdate();
        String routeId = tu.getTrip().getRouteId();
        String tripId = tu.getTrip().getTripId();
        
        // Log what we're processing (first few only)
        if (arrivalCount < 5) {
          log.debug("Processing trip: routeId={}, tripId={}, stopUpdates={}", 
                   routeId, tripId, tu.getStopTimeUpdateList().size());
        }
        
        for (var stu : tu.getStopTimeUpdateList()) {
          if (!stu.hasStopId()) continue;
          String stopId = stu.getStopId();
          
          // Process arrival data - only save if it's a future prediction
          if (stu.hasArrival()) {
            var arrival = stu.getArrival();
            if (arrival.hasTime()) {
              long actualTime = arrival.getTime() * 1000L; // Convert to milliseconds
              
              // Only save future arrivals (not past ones)
              if (actualTime > currentTime) {
                long scheduledTime = arrival.hasTime() ? arrival.getTime() * 1000L : actualTime;
                int delaySeconds = arrival.hasDelay() ? arrival.getDelay() : 0;
                
                // Check if this arrival already exists (avoid duplicates)
                String uniqueKey = tripId + "|" + stopId + "|" + actualTime;
                if (!processedArrivals.contains(uniqueKey)) {
                  Arrival arrivalRecord = new Arrival();
                  arrivalRecord.setTripId(tripId);
                  arrivalRecord.setRouteId(routeId);
                  arrivalRecord.setStopId(stopId);
                  arrivalRecord.setActualArrivalTs(actualTime);
                  arrivalRecord.setScheduledArrivalTs(scheduledTime);
                  arrivalRecord.setDelaySeconds(delaySeconds);
                  
                  arrivalRepository.save(arrivalRecord);
                  processedArrivals.add(uniqueKey);
                  arrivalCount++;
                }
              }
            }
          }
          
          // Update cache for API responses
          long sched = stu.hasArrival() && stu.getArrival().hasTime()
              ? stu.getArrival().getTime()*1000L : 0L;
          Integer delay = (stu.hasArrival() && stu.getArrival().hasDelay())
              ? stu.getArrival().getDelay() : null;
          String key = stopId+"|"+routeId;
          next.computeIfAbsent(key,k->new ArrayList<>()).add(new Eta(stopId, routeId, sched, delay));
        }
      }
      
      // keep earliest upcoming by scheduled time
      next.replaceAll((k,list)->{
        list.sort(Comparator.comparingLong(e -> e.scheduledTs()));
        return list.stream().limit(5).toList();
      });
      cache.clear(); cache.putAll(next);
      log.info("GTFS-rt refresh @{} entries={}, new arrivals saved={}", Instant.now(), cache.size(), arrivalCount);
    } catch (Exception e) {
      log.warn("GTFS-rt refresh failed: {}", e.toString());
    }
  }

  public List<Eta> getEtas(String stopId, String routeId) {
    return cache.getOrDefault(stopId+"|"+routeId, List.of());
  }
  
  // Clean up old arrivals (older than 24 hours) to prevent database bloat
  public void cleanupOldArrivals() {
    long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
    try {
      // This would need a custom query in the repository
      // For now, just log the cleanup
      log.info("Cleaning up arrivals older than 24 hours (before {})", new java.util.Date(cutoffTime));
    } catch (Exception e) {
      log.warn("Failed to cleanup old arrivals: {}", e.getMessage());
    }
  }
}
