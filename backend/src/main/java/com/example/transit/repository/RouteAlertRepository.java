package com.example.transit.repository;

import com.example.transit.model.RouteAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteAlertRepository extends JpaRepository<RouteAlert, Long> {
    
    @Query("SELECT ra FROM RouteAlert ra ORDER BY ra.createdAt DESC")
    List<RouteAlert> findAllOrderByCreatedAtDesc();
}
