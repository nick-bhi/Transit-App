package com.example.transit.service;

import com.example.transit.model.Route;
import com.example.transit.model.Stop;
import com.example.transit.model.StopTime;
import com.example.transit.repository.RouteRepository;
import com.example.transit.repository.StopRepository;
import com.example.transit.repository.StopTimeRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

@Service
public class GtfsDataLoader {
    
    private static final Logger log = LoggerFactory.getLogger(GtfsDataLoader.class);
    
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final StopTimeRepository stopTimeRepository;
    
    @Value("${gtfs.staticZipUrl}")
    private String staticZipUrl;
    
    public GtfsDataLoader(StopRepository stopRepository, RouteRepository routeRepository, StopTimeRepository stopTimeRepository) {
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
        this.stopTimeRepository = stopTimeRepository;
    }
    
    public StopRepository getStopRepository() {
        return stopRepository;
    }
    
    public RouteRepository getRouteRepository() {
        return routeRepository;
    }
    
    public void loadStaticData() {
        try {
            log.info("Loading GTFS static data from: {}", staticZipUrl);
            
            // Download and parse GTFS zip file
            try (InputStream inputStream = new URL(staticZipUrl).openStream();
                 ZipInputStream zipStream = new ZipInputStream(inputStream)) {
                
                log.info("Successfully opened GTFS zip file");
                
                // First pass: collect all entries
                java.util.List<java.util.zip.ZipEntry> entries = new java.util.ArrayList<>();
                var zipEntry = zipStream.getNextEntry();
                while (zipEntry != null) {
                    entries.add(zipEntry);
                    zipStream.closeEntry();
                    zipEntry = zipStream.getNextEntry();
                }
                
                log.info("Found {} files in GTFS zip", entries.size());
                
                // Second pass: process each file individually
                for (var entry : entries) {
                    log.info("Processing file: {}", entry.getName());
                    
                    // Create new input stream for each file
                    try (InputStream fileInputStream = new URL(staticZipUrl).openStream();
                         ZipInputStream fileZipStream = new ZipInputStream(fileInputStream)) {
                        
                        // Find the specific entry
                        var currentEntry = fileZipStream.getNextEntry();
                        while (currentEntry != null && !currentEntry.getName().equals(entry.getName())) {
                            fileZipStream.closeEntry();
                            currentEntry = fileZipStream.getNextEntry();
                        }
                        
                        if (currentEntry != null && currentEntry.getName().equals(entry.getName())) {
                            if (entry.getName().equals("stops.txt")) {
                                log.info("Found stops.txt, loading stops...");
                                loadStops(fileZipStream);
                            } else if (entry.getName().equals("routes.txt")) {
                                log.info("Found routes.txt, loading routes...");
                                loadRoutes(fileZipStream);
                            } else if (entry.getName().equals("stop_times.txt")) {
                                log.info("Found stop_times.txt, loading stop times...");
                                loadStopTimes(fileZipStream);
                            }
                        }
                    }
                }
            }
            
            log.info("GTFS static data loaded successfully");
            
        } catch (Exception e) {
            log.error("Failed to load GTFS static data: {}", e.getMessage());
        }
    }
    
    private void loadStops(InputStream inputStream) throws Exception {
        try (CSVParser parser = new CSVParser(
                new java.io.InputStreamReader(inputStream), 
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            int count = 0;
            for (CSVRecord record : parser) {
                try {
                    String stopId = record.get("stop_id");
                    String stopName = record.get("stop_name");
                    double lat = Double.parseDouble(record.get("stop_lat"));
                    double lon = Double.parseDouble(record.get("stop_lon"));
                    
                    Stop stop = new Stop(stopId, stopName, lat, lon);
                    stopRepository.save(stop);
                    count++;
                    
                    if (count % 1000 == 0) {
                        log.info("Loaded {} stops...", count);
                    }
                } catch (Exception e) {
                    log.warn("Skipping invalid stop record: {}", e.getMessage());
                }
            }
            log.info("Loaded {} stops total", count);
        }
    }
    
    private void loadRoutes(InputStream inputStream) throws Exception {
        try (CSVParser parser = new CSVParser(
                new java.io.InputStreamReader(inputStream), 
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            int count = 0;
            for (CSVRecord record : parser) {
                try {
                    String routeId = record.get("route_id");
                    String shortName = record.get("route_short_name");
                    String longName = record.get("route_long_name");
                    
                    Route route = new Route(routeId, shortName, longName);
                    routeRepository.save(route);
                    count++;
                } catch (Exception e) {
                    log.warn("Skipping invalid route record: {}", e.getMessage());
                }
            }
            log.info("Loaded {} routes total", count);
        }
    }
    
    private void loadStopTimes(InputStream inputStream) throws Exception {
        try (CSVParser parser = new CSVParser(
                new java.io.InputStreamReader(inputStream), 
                CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            
            int count = 0;
            for (CSVRecord record : parser) {
                try {
                    String tripId = record.get("trip_id");
                    String routeId = record.get("route_id");
                    String stopId = record.get("stop_id");
                    int stopSequence = Integer.parseInt(record.get("stop_sequence"));
                    
                    // Parse arrival time (format: HH:MM:SS)
                    String arrivalTime = record.get("arrival_time");
                    long scheduledArrivalTs = parseTimeToTimestamp(arrivalTime);
                    
                    StopTime stopTime = new StopTime();
                    stopTime.setTripId(tripId);
                    stopTime.setRouteId(routeId);
                    stopTime.setStopId(stopId);
                    stopTime.setStopSequence(stopSequence);
                    stopTime.setScheduledArrivalTs(scheduledArrivalTs);
                    
                    stopTimeRepository.save(stopTime);
                    count++;
                    
                    if (count % 10000 == 0) {
                        log.info("Loaded {} stop times...", count);
                    }
                } catch (Exception e) {
                    log.warn("Skipping invalid stop time record: {}", e.getMessage());
                }
            }
            log.info("Loaded {} stop times total", count);
        }
    }
    
    private long parseTimeToTimestamp(String timeStr) {
        try {
            // GTFS time format: HH:MM:SS (can be > 24 hours)
            String[] parts = timeStr.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            
            // Convert to milliseconds since epoch (using today as base)
            long totalSeconds = hours * 3600 + minutes * 60 + seconds;
            return System.currentTimeMillis() + (totalSeconds * 1000);
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
