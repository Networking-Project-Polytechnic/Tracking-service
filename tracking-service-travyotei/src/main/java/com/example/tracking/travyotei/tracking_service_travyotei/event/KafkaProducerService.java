package com.example.tracking.travyotei.tracking_service_travyotei.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KafkaProducerService  {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    /**
     * Sends a message to the specified Kafka topic.
     * @param topic The topic name.
     * @param message The message payload.
     */
    public void sendMessage(String topic, String message) {
        System.out.println(String.format("Producing message to %s: %s", topic, message));
        if(topic.equals(null) || topic.isEmpty()){
            topic="test-events";
        }
        this.kafkaTemplate.send(topic, message);
    }

    public void publish(Object message, String topic) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, json);
        } catch (Exception e) {
            // log.error("Failed to publish message to kafka: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}

