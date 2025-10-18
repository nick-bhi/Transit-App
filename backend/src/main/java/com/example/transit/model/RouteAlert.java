package com.example.transit.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "route_alerts")
public class RouteAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String startStopId;
    private String endStopId;
    private String routeId;
    private Integer alertThresholdMinutes;
    private Instant createdAt;
    
    // Default constructor required by Hibernate
    public RouteAlert() {}
    
    public RouteAlert(String startStopId, String endStopId, String routeId, Integer alertThresholdMinutes) {
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.routeId = routeId;
        this.alertThresholdMinutes = alertThresholdMinutes;
        this.createdAt = Instant.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStartStopId() { return startStopId; }
    public void setStartStopId(String startStopId) { this.startStopId = startStopId; }
    
    public String getEndStopId() { return endStopId; }
    public void setEndStopId(String endStopId) { this.endStopId = endStopId; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public Integer getAlertThresholdMinutes() { return alertThresholdMinutes; }
    public void setAlertThresholdMinutes(Integer alertThresholdMinutes) { this.alertThresholdMinutes = alertThresholdMinutes; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
