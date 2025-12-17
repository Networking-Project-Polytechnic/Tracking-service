package com.example.tracking.travyotei.tracking_service_travyotei.dto;

import org.springframework.stereotype.Component;

import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;


@Component
public class TrackingRequestDTOMapper {

    public TrackingEvent toEntity(TrackingRequestDTO dto){
        TrackingEvent event = new TrackingEvent();
        event.setBus_id(dto.getBus_id());
        event.setLatitude(dto.getLatitude());
        event.setLongitude(dto.getLongitude());
        event.setLocation(dto.getLocation());
        event.setStatus(dto.getStatus());
        event.setTimestamp(dto.getTimestamp());

        return event;
    }

    public TrackingResponseDTO toResponseDTO(TrackingEvent entity){
        return new TrackingResponseDTO(
            entity.getBus_id(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getStatus(),
            entity.getLocation(),
            entity.getTimestamp()
        );
    }

}
