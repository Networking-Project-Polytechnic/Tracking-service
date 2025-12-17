package com.example.tracking.travyotei.tracking_service_travyotei.service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;
import com.example.tracking.travyotei.tracking_service_travyotei.model.TrackingEvent;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.BusLocationStatusRepository;
import com.example.tracking.travyotei.tracking_service_travyotei.repository.TrackingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Its primary function is to periodically process all incoming tracking events, determine the absolute latest location and status for every single bus,
//  persist that real-time summary to a database table, and broadcast the current status
//  to any connected clients (via WebSockets).
@RequiredArgsConstructor
@Slf4j
@Service
public class ScheduledUpdateService {

    private final TrackingRepository trackingRepository;
    private final BusLocationStatusRepository busLocationStatusRepository;
    private final LocationBroadcastService locationBroadcastService;

    // More frequent: update and broadcast real-time state every 2s (configurable)
    @Scheduled(fixedRateString = "${app.update.bus.fixed-rate-ms:2000}")
    @Transactional
    public void updateBusStatuses() {
        List<TrackingEvent> events = trackingRepository.findAll();
        Map<UUID, TrackingEvent> latestPerBus = new HashMap<>();
        for (TrackingEvent e : events) {
            UUID busId = e.getBus_id();
            if (busId == null) continue;
            TrackingEvent current = latestPerBus.get(busId);
            if (current == null || (e.getTimestamp() != null && current.getTimestamp() != null && e.getTimestamp().isAfter(current.getTimestamp()))) {
                latestPerBus.put(busId, e);
            }
        }

        for (Map.Entry<UUID, TrackingEvent> entry : latestPerBus.entrySet()) {
            try {
                TrackingEvent latest = entry.getValue();
                Optional<BusLocationStatus> opt = busLocationStatusRepository.findByBusId(entry.getKey());
                BusLocationStatus status = opt.orElseGet(() -> {
                    BusLocationStatus b = new BusLocationStatus();
                    b.setBus_id(entry.getKey());
                    return b;
                });

                // detect change (latitude / longitude / status)
                boolean changed = !Objects.equals(status.getCurrent_latitude(), latest.getLatitude())
                        || !Objects.equals(status.getCurrent_longitude(), latest.getLongitude())
                        || !Objects.equals(status.getCurrent_status(), latest.getStatus());

                // always update last_updated so UI knows we saw the bus
                status.setLast_updated(LocalTime.now());

                if (changed) {
                    status.setCurrent_latitude(latest.getLatitude());
                    status.setCurrent_longitude(latest.getLongitude());
                    status.setCurrent_status(latest.getStatus());
                    busLocationStatusRepository.save(status);
                    locationBroadcastService.broadcast(status);
                    log.debug("Broadcasted location for bus {}", entry.getKey());
                } else {
                    // persist last_updated alone (optional)
                    busLocationStatusRepository.save(status);
                }
            } catch (Exception ex) {
                log.error("Error updating bus status for {}: {}", entry.getKey(), ex.getMessage(), ex);
            }
        }
    }

    // Less frequent: perform heavier TrackingEvent tasks (e.g., cleanup or aggregations)
    @Scheduled(fixedRateString = "${app.update.event.fixed-rate-ms:30000}")
    public void periodicEventTasks() {
        // example: no-op placeholder — add persistence/aggregation/cleanup logic here
    }
}