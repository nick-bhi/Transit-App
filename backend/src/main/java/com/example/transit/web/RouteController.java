package com.example.transit.web;

import com.example.transit.model.RouteAlert;
import com.example.transit.model.RouteSummary;
import com.example.transit.model.Stop;
import com.example.transit.model.Route;
import com.example.transit.repository.RouteAlertRepository;
import com.example.transit.repository.StopRepository;
import com.example.transit.repository.RouteRepository;
import com.example.transit.service.RouteAnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    
    private final RouteAlertRepository routeAlertRepository;
    private final RouteAnalyticsService routeAnalyticsService;
    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    
    public RouteController(RouteAlertRepository routeAlertRepository, 
                          RouteAnalyticsService routeAnalyticsService,
                          StopRepository stopRepository,
                          RouteRepository routeRepository) {
        this.routeAlertRepository = routeAlertRepository;
        this.routeAnalyticsService = routeAnalyticsService;
        this.stopRepository = stopRepository;
        this.routeRepository = routeRepository;
    }
    
    @PostMapping("/alerts")
    public RouteAlert createRouteAlert(@RequestBody CreateRouteAlertRequest request) {
        RouteAlert alert = new RouteAlert(
            request.getStartStopId(),
            request.getEndStopId(),
            request.getRouteId(),
            request.getAlertThresholdMinutes()
        );
        return routeAlertRepository.save(alert);
    }
    
    @GetMapping("/alerts")
    public List<RouteAlert> getAllRouteAlerts() {
        return routeAlertRepository.findAllOrderByCreatedAtDesc();
    }
    
    @GetMapping("/summary")
    public RouteSummary getRouteSummary(@RequestParam String startStopId,
                                       @RequestParam String endStopId,
                                       @RequestParam String routeId) {
        return routeAnalyticsService.getRouteSummary(startStopId, endStopId, routeId);
    }
    
    @DeleteMapping("/alerts/{id}")
    public void deleteRouteAlert(@PathVariable Long id) {
        routeAlertRepository.deleteById(id);
    }
    
    @GetMapping("/stops")
    public List<Stop> getAllStops() {
        return stopRepository.findAll();
    }
    
    @GetMapping("/routes")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    @GetMapping("/stops/search")
    public List<Stop> searchStops(@RequestParam String query) {
        // This would need a custom query for name search
        // For now, return all stops (frontend can filter)
        return stopRepository.findAll();
    }
    
    // Request DTO
    public static class CreateRouteAlertRequest {
        private String startStopId;
        private String endStopId;
        private String routeId;
        private Integer alertThresholdMinutes;
        
        // Getters and setters
        public String getStartStopId() { return startStopId; }
        public void setStartStopId(String startStopId) { this.startStopId = startStopId; }
        
        public String getEndStopId() { return endStopId; }
        public void setEndStopId(String endStopId) { this.endStopId = endStopId; }
        
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        
        public Integer getAlertThresholdMinutes() { return alertThresholdMinutes; }
        public void setAlertThresholdMinutes(Integer alertThresholdMinutes) { this.alertThresholdMinutes = alertThresholdMinutes; }
    }
}
