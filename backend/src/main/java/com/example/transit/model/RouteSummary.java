package com.example.transit.model;

import java.time.Instant;

public class RouteSummary {
    private String startStopId;
    private String endStopId;
    private String routeId;
    private String startStopName;
    private String endStopName;
    private String routeName;
    private Long nextScheduledArrival;
    private Integer totalTravelTimeMinutes;
    private Double avgDelayMinutes;
    private Double onTimePercentage;
    
    public RouteSummary() {}
    
    public RouteSummary(String startStopId, String endStopId, String routeId, 
                       String startStopName, String endStopName, String routeName,
                       Long nextScheduledArrival, Integer totalTravelTimeMinutes,
                       Double avgDelayMinutes, Double onTimePercentage) {
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.routeId = routeId;
        this.startStopName = startStopName;
        this.endStopName = endStopName;
        this.routeName = routeName;
        this.nextScheduledArrival = nextScheduledArrival;
        this.totalTravelTimeMinutes = totalTravelTimeMinutes;
        this.avgDelayMinutes = avgDelayMinutes;
        this.onTimePercentage = onTimePercentage;
    }
    
    // Getters and setters
    public String getStartStopId() { return startStopId; }
    public void setStartStopId(String startStopId) { this.startStopId = startStopId; }
    
    public String getEndStopId() { return endStopId; }
    public void setEndStopId(String endStopId) { this.endStopId = endStopId; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public String getStartStopName() { return startStopName; }
    public void setStartStopName(String startStopName) { this.startStopName = startStopName; }
    
    public String getEndStopName() { return endStopName; }
    public void setEndStopName(String endStopName) { this.endStopName = endStopName; }
    
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    
    public Long getNextScheduledArrival() { return nextScheduledArrival; }
    public void setNextScheduledArrival(Long nextScheduledArrival) { this.nextScheduledArrival = nextScheduledArrival; }
    
    public Integer getTotalTravelTimeMinutes() { return totalTravelTimeMinutes; }
    public void setTotalTravelTimeMinutes(Integer totalTravelTimeMinutes) { this.totalTravelTimeMinutes = totalTravelTimeMinutes; }
    
    public Double getAvgDelayMinutes() { return avgDelayMinutes; }
    public void setAvgDelayMinutes(Double avgDelayMinutes) { this.avgDelayMinutes = avgDelayMinutes; }
    
    public Double getOnTimePercentage() { return onTimePercentage; }
    public void setOnTimePercentage(Double onTimePercentage) { this.onTimePercentage = onTimePercentage; }
}
