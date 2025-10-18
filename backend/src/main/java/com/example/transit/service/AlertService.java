package com.example.transit.service;

import com.example.transit.model.RouteAlert;
import com.example.transit.repository.RouteAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {
    
    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    
    private final RouteAlertRepository routeAlertRepository;
    private final RouteAnalyticsService routeAnalyticsService;
    
    public AlertService(RouteAlertRepository routeAlertRepository, 
                       RouteAnalyticsService routeAnalyticsService) {
        this.routeAlertRepository = routeAlertRepository;
        this.routeAnalyticsService = routeAnalyticsService;
    }
    
    @Scheduled(fixedRate = 60000) // Check every minute
    public void checkAlerts() {
        try {
            List<RouteAlert> alerts = routeAlertRepository.findAll();
            long currentTime = System.currentTimeMillis();
            
            for (RouteAlert alert : alerts) {
                checkSingleAlert(alert, currentTime);
            }
        } catch (Exception e) {
            log.error("Error checking alerts: {}", e.getMessage());
        }
    }
    
    private void checkSingleAlert(RouteAlert alert, long currentTime) {
        try {
            // Get current route summary
            var summary = routeAnalyticsService.getRouteSummary(
                alert.getStartStopId(), 
                alert.getEndStopId(), 
                alert.getRouteId()
            );
            
            // Check if next arrival exceeds threshold
            if (summary.getNextScheduledArrival() != null) {
                long timeUntilArrival = (summary.getNextScheduledArrival() - currentTime) / (1000 * 60); // minutes
                
                if (timeUntilArrival > alert.getAlertThresholdMinutes()) {
                    log.info("ALERT: Route {} from {} to {} - Next arrival in {} minutes (threshold: {} minutes)", 
                            alert.getRouteId(), alert.getStartStopId(), alert.getEndStopId(), 
                            timeUntilArrival, alert.getAlertThresholdMinutes());
                    
                    // Here you could send notifications, emails, etc.
                    sendAlert(alert, timeUntilArrival);
                }
            }
        } catch (Exception e) {
            log.warn("Error checking alert for route {}: {}", alert.getRouteId(), e.getMessage());
        }
    }
    
    private void sendAlert(RouteAlert alert, long timeUntilArrival) {
        // This is where you would integrate with notification services
        // For now, just log the alert
        log.warn("🚨 ALERT: Route {} is running {} minutes late! Expected arrival in {} minutes", 
                alert.getRouteId(), timeUntilArrival - alert.getAlertThresholdMinutes(), timeUntilArrival);
    }
}
