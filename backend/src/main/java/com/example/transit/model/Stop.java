package com.example.transit.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stops")
public class Stop {
    @Id
    private String id;
    
    private String name;
    
    private Double lat;
    
    private Double lon;
    
    // Default constructor required by Hibernate
    public Stop() {}
    
    public Stop(String id, String name, Double lat, Double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    
    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }
}

