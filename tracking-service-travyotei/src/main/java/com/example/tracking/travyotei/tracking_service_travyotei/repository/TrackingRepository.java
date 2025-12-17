package com.example.tracking.travyotei.tracking_service_travyotei.repository;

import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// This class handles database operations for TrackingEvent entities
@Repository
public interface TrackingRepository extends JpaRepository<TrackingEvent, UUID> {

    // Use the @Query annotation to write the query manually (JPQL syntax)
    @Query("SELECT t FROM TrackingEvent t WHERE t.bus_id = :busId ORDER BY t.timestamp ASC")
    List<TrackingEvent> findEventsByBusIdSorted(@Param("busId") UUID busId);
    
}
