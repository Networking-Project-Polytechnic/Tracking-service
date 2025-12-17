package com.example.tracking.travyotei.tracking_service_travyotei.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingResponseDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
// import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
import com.example.tracking.travyotei.tracking_service_travyotei.service.TrackingService;

import lombok.RequiredArgsConstructor;


// This class will handle HTTP requests related to tracking events
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {
    private final TrackingService trackingService;

     @PostMapping("/update")
    public ResponseEntity<TrackingResponseDTO> updateTracking(@RequestBody TrackingRequestDTO request) {
        // persist event (TrackingService should save TrackingEvent)
        TrackingResponseDTO saved = trackingService.addTrackingEvent(request);
        
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{bus_Id}")
    public ResponseEntity<List<TrackingEvent>> getTracking(@PathVariable UUID bus_Id) {
        // return historical events or recent events for initialization
        List<TrackingEvent> history = trackingService.getTrackingHistory(bus_Id);
        return ResponseEntity.ok(history);
    }

    

}
