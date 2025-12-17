package com.example.tracking.travyotei.tracking_service_travyotei.event;

import java.util.UUID;
import java.time.LocalTime;
import java.util.Optional;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.tracking.travyotei.tracking_service_travyotei.dto.PackageEventDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTO;
import com.example.tracking.travyotei.tracking_service_travyotei.dto.TrackingRequestDTOMapper;
import com.example.tracking.travyotei.tracking_service_travyotei.geocoding_service.NominatimGeocoding;
import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;
import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.BusLocationStatusRepository;
// import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.TrackingRepository;
import com.example.tracking.travyotei.tracking_service_travyotei.service.LocationBroadcastService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KafkaConsumerService {
    private final TrackingRepository trackingRepository;
    private final BusLocationStatusRepository busLocationStatusRepository;
    private final LocationBroadcastService locationBroadcastService;
    private final NominatimGeocoding nominatimGeocoding;
    private final TrackingRequestDTOMapper trackingRequestDTOMapper;
    private final ObjectMapper objectMapper;

    /**
     * Listens for messages on the "test-events" topic.
     * The consumer group ID is defined in application.properties (e.g., my-application-group).
     * @param message The received message payload.
     */
    @KafkaListener(topics = "${app.kafka.topic.locations:bus-locations}", groupId = "tracking-service-group")
    public void consume(String raw) {
        try {
            TrackingRequestDTO msg = objectMapper.readValue(raw, TrackingRequestDTO.class);

            // Build and persist TrackingEvent (audit)
            TrackingRequestDTO dtoToSave = new TrackingRequestDTO();
            dtoToSave.setBus_id(msg.getBus_id());
            dtoToSave.setLatitude(msg.getLatitude());
            dtoToSave.setLongitude(msg.getLongitude());
            dtoToSave.setStatus(msg.getStatus());
            dtoToSave.setLocation(msg.getLocation());
            if (msg.getTimestamp() != null) {
                dtoToSave.setTimestamp(msg.getTimestamp());
            } else {
                dtoToSave.setTimestamp(LocalTime.now());
            }
            TrackingEvent savedEvent = trackingRequestDTOMapper.toEntity(dtoToSave);
            trackingRepository.save(savedEvent);

            // Upsert BusLocationStatus and broadcast immediately (real-time)
            if (savedEvent.getBus_id() != null) {
                Optional<BusLocationStatus> opt = busLocationStatusRepository.findByBusId(savedEvent.getBus_id());
                BusLocationStatus status = opt.orElseGet(() -> {
                    BusLocationStatus b = new BusLocationStatus();
                    b.setBus_id(savedEvent.getBus_id());
                    return b;
                });

                status.setCurrent_latitude(savedEvent.getLatitude());
                status.setCurrent_longitude(savedEvent.getLongitude());
                status.setCurrent_status(savedEvent.getStatus());
                status.setLast_updated(LocalTime.now());
                busLocationStatusRepository.save(status);

                // broadcast to websocket subscribers
                try {
                    locationBroadcastService.broadcast(status);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    // log.warn("Failed to broadcast location for {}: {}", savedEvent.getBus_id(), e.getMessage());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            // log.error("Failed to consume tracking message: {}", ex.getMessage(), ex);
        }
    }
    @KafkaListener(topics = "another-topic", groupId = "my-application-group")
    public void packageCreated(String message) {
        // ObjectMapper mapper = new ObjectMapper();
        try {
            // JsonNode node = mapper.readTree(message);
            
        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "package-created", groupId = "tracking-service")
    public void handlePackageCreated(String raw) {
        try {
            // Explicitly parse the incoming raw JSON string using the configured ObjectMapper
            PackageEventDTO packageEventDTO = objectMapper.readValue(raw, PackageEventDTO.class);

            TrackingEvent initialEventLog = new TrackingEvent();

            UUID bus_Id = packageEventDTO.getBus_id();
            String status = packageEventDTO.getStatus();
            String locationName = packageEventDTO.getStart_route();
            // This line now works because objectMapper.readValue() handles the LocalTime conversion
            LocalTime departureTime = packageEventDTO.getDeparture_time(); 

            initialEventLog.setBus_id(bus_Id);
            initialEventLog.setStatus(status);
            initialEventLog.setLocation(locationName);
            // Ensure your TrackingEvent entity timestamp field is a LocalTime or compatible type
            initialEventLog.setTimestamp(departureTime);

            trackingRepository.save(initialEventLog);

            // ... your existing geocoding logic here ...
            try {
                // Call the geocoding service, which returns an Optional<JSONObject>
                Optional<JSONObject> coordinatesOptional = nominatimGeocoding.getCoordinatesFromPlaceName(locationName);
                if (coordinatesOptional.isPresent()) {
                    JSONObject coordinatesJson = coordinatesOptional.get();

                    String latString = coordinatesJson.getString("lat");
                    Double lat = Double.valueOf(latString);

                    String lonString = coordinatesJson.getString("lon");
                    Double lon = Double.valueOf(lonString);

                    initialEventLog.setLatitude(lat);
                    initialEventLog.setLongitude(lon);
                    
                    // Save again after updating coordinates
                    trackingRepository.save(initialEventLog); 
                } else {
                     System.out.println("No coordinates found for location: " + locationName);
                }
            } catch (Exception e) {
                 System.out.println("Error during geocoding or coordinate assignment: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error processing the package event: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

