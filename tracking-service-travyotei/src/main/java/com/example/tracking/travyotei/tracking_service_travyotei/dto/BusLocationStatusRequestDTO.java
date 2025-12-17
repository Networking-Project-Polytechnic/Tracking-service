package com.example.tracking.travyotei.tracking_service_travyotei.dto;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusLocationStatusRequestDTO {
    private UUID bus_id;
    private Double current_latitude;
    private Double current_longitude;   
    private String current_status; 
    private LocalTime last_updated;
}
