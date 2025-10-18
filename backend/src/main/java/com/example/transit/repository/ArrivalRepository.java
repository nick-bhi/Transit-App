package com.example.transit.repository;

import com.example.transit.model.Arrival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, Long> {
    
    @Query("SELECT a FROM Arrival a WHERE a.stopId = :stopId AND a.routeId = :routeId AND a.actualArrivalTs > :currentTime ORDER BY a.actualArrivalTs ASC")
    List<Arrival> findUpcomingArrivals(@Param("stopId") String stopId, @Param("routeId") String routeId, @Param("currentTime") Long currentTime);
    
    @Query("SELECT AVG(a.delaySeconds) FROM Arrival a WHERE a.stopId = :stopId AND a.routeId = :routeId AND a.actualArrivalTs >= :since")
    Double findAverageDelay(@Param("stopId") String stopId, @Param("routeId") String routeId, @Param("since") Long since);
    
    @Query("SELECT COUNT(a) FROM Arrival a WHERE a.stopId = :stopId AND a.routeId = :routeId AND a.actualArrivalTs >= :since")
    Long countArrivals(@Param("stopId") String stopId, @Param("routeId") String routeId, @Param("since") Long since);
    
    @Query("SELECT COUNT(a) FROM Arrival a WHERE a.stopId = :stopId AND a.routeId = :routeId AND a.actualArrivalTs >= :since AND a.delaySeconds <= 0")
    Long countOnTimeArrivals(@Param("stopId") String stopId, @Param("routeId") String routeId, @Param("since") Long since);
}

