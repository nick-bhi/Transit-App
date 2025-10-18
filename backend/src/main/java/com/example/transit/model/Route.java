package com.example.transit.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    private String id;
    
    private String shortName;
    
    private String longName;
    
    // Default constructor required by Hibernate
    public Route() {}
    
    public Route(String id, String shortName, String longName) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    
    public String getLongName() { return longName; }
    public void setLongName(String longName) { this.longName = longName; }
}

