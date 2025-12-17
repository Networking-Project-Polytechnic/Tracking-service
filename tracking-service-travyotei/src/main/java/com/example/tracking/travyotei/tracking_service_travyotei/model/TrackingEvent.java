package com.example.tracking.travyotei.tracking_service_travyotei.model;

import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// This is the Audit Log or the Event Source of the package's journey.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TrackingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID bus_id;
    private Double latitude;
    private Double longitude;
    private String status; // Initial value is "At Origin Terminal"
    private String location;
    private LocalTime timestamp;
}
