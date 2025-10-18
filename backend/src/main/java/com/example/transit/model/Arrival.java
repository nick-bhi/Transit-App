package com.example.transit.model;

import jakarta.persistence.*;

@Entity
@Table(name = "arrivals")
public class Arrival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tripId;
    
    private String routeId;
    
    private String stopId;
    
    private Long actualArrivalTs;
    
    private Long scheduledArrivalTs;
    
    private Integer delaySeconds;
    
    // Default constructor required by Hibernate
    public Arrival() {}
    
    public Arrival(String tripId, String routeId, String stopId, Long actualArrivalTs, Long scheduledArrivalTs, Integer delaySeconds) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.stopId = stopId;
        this.actualArrivalTs = actualArrivalTs;
        this.scheduledArrivalTs = scheduledArrivalTs;
        this.delaySeconds = delaySeconds;
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
    
    public Long getActualArrivalTs() { return actualArrivalTs; }
    public void setActualArrivalTs(Long actualArrivalTs) { this.actualArrivalTs = actualArrivalTs; }
    
    public Long getScheduledArrivalTs() { return scheduledArrivalTs; }
    public void setScheduledArrivalTs(Long scheduledArrivalTs) { this.scheduledArrivalTs = scheduledArrivalTs; }
    
    public Integer getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(Integer delaySeconds) { this.delaySeconds = delaySeconds; }
}

