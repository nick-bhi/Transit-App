package com.example.transit.service;

import com.example.transit.model.RouteSummary;
import com.example.transit.repository.ArrivalRepository;
import com.example.transit.repository.RouteRepository;
import com.example.transit.repository.StopRepository;
import com.example.transit.repository.StopTimeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RouteAnalyticsService {
    
    private static final Logger log = LoggerFactory.getLogger(RouteAnalyticsService.class);
    
    private final ArrivalRepository arrivalRepository;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final StopTimeRepository stopTimeRepository;
    
    public RouteAnalyticsService(ArrivalRepository arrivalRepository, 
                                StopRepository stopRepository,
                                RouteRepository routeRepository,
                                StopTimeRepository stopTimeRepository) {
        this.arrivalRepository = arrivalRepository;
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
        this.stopTimeRepository = stopTimeRepository;
    }
    
    public RouteSummary getRouteSummary(String startStopId, String endStopId, String routeId) {
        try {
            // Get stop and route names
            String startStopName = stopRepository.findById(startStopId)
                .map(stop -> stop.getName()).orElse("Unknown Stop");
            String endStopName = stopRepository.findById(endStopId)
                .map(stop -> stop.getName()).orElse("Unknown Stop");
            String routeName = routeRepository.findById(routeId)
                .map(route -> route.getShortName() + " - " + route.getLongName()).orElse("Unknown Route");
            
            // Calculate next scheduled arrival (simplified - would need more complex logic)
            Long nextScheduledArrival = calculateNextScheduledArrival(startStopId, routeId);
            
            // Calculate total travel time (simplified)
            Integer totalTravelTimeMinutes = calculateTravelTime(startStopId, endStopId, routeId);
            
            // Calculate average delay over past 7 days
            Double avgDelayMinutes = calculateAverageDelay(startStopId, routeId);
            
            // Calculate on-time percentage
            Double onTimePercentage = calculateOnTimePercentage(startStopId, routeId);
            
            return new RouteSummary(
                startStopId, endStopId, routeId,
                startStopName, endStopName, routeName,
                nextScheduledArrival, totalTravelTimeMinutes,
                avgDelayMinutes, onTimePercentage
            );
            
        } catch (Exception e) {
            log.error("Error calculating route summary: {}", e.getMessage());
            return createEmptySummary(startStopId, endStopId, routeId);
        }
    }
    
    private Long calculateNextScheduledArrival(String stopId, String routeId) {
        try {
            long currentTime = System.currentTimeMillis();
            
            // First try to get from real-time arrivals (most accurate)
            List<com.example.transit.model.Arrival> upcomingArrivals = arrivalRepository.findUpcomingArrivals(stopId, routeId, currentTime);
            if (!upcomingArrivals.isEmpty()) {
                return upcomingArrivals.get(0).getActualArrivalTs();
            }
            
            // Fallback to scheduled times
            List<com.example.transit.model.StopTime> upcomingScheduled = stopTimeRepository.findUpcomingScheduledTimes(stopId, routeId, currentTime);
            if (!upcomingScheduled.isEmpty()) {
                return upcomingScheduled.get(0).getScheduledArrivalTs();
            }
            
            // If no data available, return current time + 5 minutes
            return currentTime + (5 * 60 * 1000);
        } catch (Exception e) {
            log.warn("Error calculating next scheduled arrival: {}", e.getMessage());
            return System.currentTimeMillis() + (5 * 60 * 1000);
        }
    }
    
    private Integer calculateTravelTime(String startStopId, String endStopId, String routeId) {
        try {
            // Find stop times for both stops on the same route
            List<com.example.transit.model.StopTime> startTimes = stopTimeRepository.findByStopIdAndRouteId(startStopId, routeId);
            List<com.example.transit.model.StopTime> endTimes = stopTimeRepository.findByStopIdAndRouteId(endStopId, routeId);
            
            if (startTimes.isEmpty() || endTimes.isEmpty()) {
                return 15; // Default 15 minutes
            }
            
            // Find the earliest time difference between start and end stops
            int minTravelTime = Integer.MAX_VALUE;
            for (com.example.transit.model.StopTime startTime : startTimes) {
                for (com.example.transit.model.StopTime endTime : endTimes) {
                    if (startTime.getTripId().equals(endTime.getTripId())) {
                        long timeDiff = endTime.getScheduledArrivalTs() - startTime.getScheduledArrivalTs();
                        if (timeDiff > 0 && timeDiff < minTravelTime) {
                            minTravelTime = (int) (timeDiff / (1000 * 60)); // Convert to minutes
                        }
                    }
                }
            }
            
            return minTravelTime == Integer.MAX_VALUE ? 15 : minTravelTime;
        } catch (Exception e) {
            log.warn("Error calculating travel time: {}", e.getMessage());
            return 15;
        }
    }
    
    private Double calculateAverageDelay(String stopId, String routeId) {
        try {
            long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
            Double avgDelay = arrivalRepository.findAverageDelay(stopId, routeId, sevenDaysAgo);
            return avgDelay != null ? avgDelay / 60.0 : 0.0; // Convert seconds to minutes
        } catch (Exception e) {
            log.warn("Error calculating average delay: {}", e.getMessage());
            return 0.0;
        }
    }
    
    private Double calculateOnTimePercentage(String stopId, String routeId) {
        try {
            long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
            
            Long totalArrivals = arrivalRepository.countArrivals(stopId, routeId, sevenDaysAgo);
            Long onTimeArrivals = arrivalRepository.countOnTimeArrivals(stopId, routeId, sevenDaysAgo);
            
            if (totalArrivals == 0) {
                return 100.0; // No data, assume 100% on-time
            }
            
            return (onTimeArrivals.doubleValue() / totalArrivals.doubleValue()) * 100.0;
        } catch (Exception e) {
            log.warn("Error calculating on-time percentage: {}", e.getMessage());
            return 100.0;
        }
    }
    
    private RouteSummary createEmptySummary(String startStopId, String endStopId, String routeId) {
        return new RouteSummary(
            startStopId, endStopId, routeId,
            "Unknown", "Unknown", "Unknown",
            System.currentTimeMillis() + (5 * 60 * 1000), // 5 minutes from now
            15, // 15 minutes travel time
            0.0, // 0 minutes average delay
            100.0 // 100% on-time
        );
    }
}
