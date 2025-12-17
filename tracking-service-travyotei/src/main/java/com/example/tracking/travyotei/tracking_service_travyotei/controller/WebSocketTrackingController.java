
package com.example.tracking.travyotei.tracking_service_travyotei.controller;

import java.time.LocalTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.event.KafkaProducerService;
import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.BusLocationStatusRepository;
import com.example.tracking.travyotei.tracking_service_travyotei.service.LocationBroadcastService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class WebSocketTrackingController {

    private final KafkaProducerService kafkaProducer;
    private final BusLocationStatusRepository busLocationStatusRepository;
    private final LocationBroadcastService locationBroadcastService;
    private static final Logger log = LoggerFactory.getLogger(WebSocketTrackingController.class);
    /**
     * STOMP destination: send to /app/locations
     * Clients (buses) should send message payload matching TrackingRequestDTO JSON
     * This handler:
     *  - updates BusLocationStatus immediately and broadcasts to /topic/locations
     *  - publishes the payload to Kafka for durable audit (consumer will persist TrackingEvent)
     */
    @MessageMapping("/locations")
    public void handleLocationUpdate(@Payload TrackingRequestDTO dto) {
        try {
            log.debug("Received WS location DTO: {}", dto);
            // upsert BusLocationStatus immediately
            if (dto.getBus_id() != null) {
                Optional<BusLocationStatus> opt = busLocationStatusRepository.findByBusId(dto.getBus_id());
                BusLocationStatus status = opt.orElseGet(() -> {
                    BusLocationStatus b = new BusLocationStatus();
                    b.setBus_id(dto.getBus_id());
                    return b;
                });

                status.setCurrent_latitude(dto.getLatitude());
                status.setCurrent_longitude(dto.getLongitude());
                status.setCurrent_status(dto.getStatus());
                status.setLast_updated(dto.getTimestamp() != null ? dto.getTimestamp() : LocalTime.now());

                busLocationStatusRepository.save(status);
                log.debug("Upserted BusLocationStatus for {}", dto.getBus_id());
                // broadcast over websocket to subscribers
                try {
                    locationBroadcastService.broadcast(status);
                } catch (Exception e) {
                    log.warn("Broadcast failed after websocket update: {}", e.getMessage());
                }
            }

            // publish message to Kafka for audit (consumer will persist TrackingEvent)
            kafkaProducer.publish(dto, "location_update");

        } catch (Exception e) {
            log.error("Failed processing websocket location update: {}", e.getMessage(), e);
        }
    }
}