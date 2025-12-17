package com.example.tracking.travyotei.tracking_service_travyotei.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTO;
// import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequest;
import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTOMapper;
import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingResponseDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.event.KafkaProducerService;
import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.TrackingRepository;

import lombok.RequiredArgsConstructor;

// This class will contain business logic related to tracking events
// RequiredArgsConstructor makes constructors of all @NonNull annotated and final variables only
@RequiredArgsConstructor
@Service
public class TrackingService {
    private final TrackingRepository trackingRepository;
    private final KafkaProducerService kafkaProducer;
    private final TrackingRequestDTOMapper trackingRequestDTOMapper;
    
    
    public TrackingResponseDTO addTrackingEvent(TrackingRequestDTO ev) {
        

        TrackingEvent event = trackingRequestDTOMapper.toEntity(ev);
        TrackingEvent savedEvent = trackingRepository.save(event);
        return trackingRequestDTOMapper.toResponseDTO(savedEvent);
    }

    // Return full history for a given trackingCode (bus). If not a UUID, returns empty list.
    public List<TrackingEvent> getTrackingHistory(UUID bus_id) {
        List<TrackingEvent> events = trackingRepository.findEventsByBusIdSorted(bus_id);
        return events;
    }

    

}
