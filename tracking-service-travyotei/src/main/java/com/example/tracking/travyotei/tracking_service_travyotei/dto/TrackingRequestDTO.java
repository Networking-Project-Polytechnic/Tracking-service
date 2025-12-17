package com.example.tracking.travyotei.tracking_service_travyotei.dto;


import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//  This is the entity for storing the tracking data of each bus
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingRequestDTO {

    private UUID bus_id;
    private Double latitude;
    private Double longitude;
    private String status; // Initial value is "At Origin Terminal"
    private String location;
    private LocalTime timestamp;

}
