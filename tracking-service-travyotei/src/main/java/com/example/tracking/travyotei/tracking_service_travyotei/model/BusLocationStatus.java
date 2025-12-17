package com.example.tracking.travyotei.tracking_service_travyotei.model;

import java.util.UUID;
import java.time.LocalTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



// This is the Queryable, Real-Time State optimized for display on your Leaflet map.

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BusLocationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID bus_id;
    private Double current_latitude;
    private Double current_longitude;   
    private String current_status; 
    private LocalTime last_updated;
}
