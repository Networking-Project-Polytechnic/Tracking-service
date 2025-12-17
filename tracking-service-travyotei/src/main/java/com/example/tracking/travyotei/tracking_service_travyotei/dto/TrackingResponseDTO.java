package com.example.tracking.travyotei.tracking_service_travyotei.dto;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponseDTO {
    private UUID bus_id;
    private Double latitude;
    private Double longitude;
    private String status; // Initial value is "At Origin Terminal"
    private String location;
    private LocalTime timestamp;
}
