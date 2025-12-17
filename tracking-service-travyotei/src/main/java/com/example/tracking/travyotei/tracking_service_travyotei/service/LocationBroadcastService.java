package com.example.tracking.travyotei.tracking_service_travyotei.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.tracking.travyotei.tracking_service_travyotei.model.BusLocationStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LocationBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;
    private static final String DEST = "/topic/locations";
    private static final Logger log = LoggerFactory.getLogger(LocationBroadcastService.class);
    
    public void broadcast(BusLocationStatus status) {
        if (status == null) {
            System.err.println("broadcast called with null status");
            return;
        }
        log.debug("Broadcasting status for bus {} lat={} lon={} status={}",
            status.getBus_id(), status.getCurrent_latitude(), status.getCurrent_longitude(), status.getCurrent_status());
        messagingTemplate.convertAndSend(DEST, status);
    }
}