package com.example.transit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stop_times")
public class StopTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tripId;
    
    private String routeId;
    
    private String stopId;
    
    private Integer stopSequence;
    
    private Long scheduledArrivalTs;
    
    // Default constructor required by Hibernate
    public StopTime() {}
    
    public StopTime(String tripId, String routeId, String stopId, Integer stopSequence, Long scheduledArrivalTs) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.scheduledArrivalTs = scheduledArrivalTs;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; }
    
    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }
    
    public Long getScheduledArrivalTs() { return scheduledArrivalTs; }
    public void setScheduledArrivalTs(Long scheduledArrivalTs) { this.scheduledArrivalTs = scheduledArrivalTs; }
}

