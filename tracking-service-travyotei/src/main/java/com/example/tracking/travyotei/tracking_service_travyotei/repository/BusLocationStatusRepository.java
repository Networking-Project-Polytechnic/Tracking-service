package com.example.tracking.travyotei.tracking_service_travyotei.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;

@Repository
public interface BusLocationStatusRepository extends JpaRepository<BusLocationStatus, UUID> {

    @Query("select b from BusLocationStatus b where b.bus_id = :busId")
    Optional<BusLocationStatus> findByBusId(@Param("busId") UUID busId);
    
}
