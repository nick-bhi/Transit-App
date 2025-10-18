package com.example.transit.repository;

import com.example.transit.model.StopTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, Long> {
    List<StopTime> findByStopIdAndRouteId(String stopId, String routeId);
    
    @Query("SELECT st FROM StopTime st WHERE st.stopId = :stopId AND st.routeId = :routeId AND st.scheduledArrivalTs > :currentTime ORDER BY st.scheduledArrivalTs ASC")
    List<StopTime> findUpcomingScheduledTimes(@Param("stopId") String stopId, @Param("routeId") String routeId, @Param("currentTime") Long currentTime);
    
    @Query("SELECT st FROM StopTime st WHERE st.routeId = :routeId AND st.stopId IN (:stopIds) ORDER BY st.stopSequence ASC")
    List<StopTime> findRouteStopsInOrder(@Param("routeId") String routeId, @Param("stopIds") List<String> stopIds);
}

