package com.example.tracking.travyotei.tracking_service_travyotei.dto;


import java.time.LocalTime;
import java.util.UUID;

// Define this class to match your Kafka payload structure
public class PackageEventDTO {
    private UUID bus_id;
    private String status;
    private String start_route; // Matches the JSON key "start_route"
    private LocalTime departure_time; // Matches the JSON key "departure_time"
    
    // Getters and setters (required for Jackson deserialization)
    public UUID getBus_id() { return bus_id; }
    public void setBus_id(UUID bus_id) { this.bus_id = bus_id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStart_route() { return start_route; }
    public void setStart_route(String start_route) { this.start_route = start_route; }
    public LocalTime getDeparture_time() { return departure_time; }
    public void setDeparture_time(LocalTime departure_time) { this.departure_time = departure_time; }
}
