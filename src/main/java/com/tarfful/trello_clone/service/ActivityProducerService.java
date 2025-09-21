package com.tarfful.trello_clone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.topic.activity}")
    private String activityTopic;

    public void sendActivityMessage(String message){
        try{
            kafkaTemplate.send(activityTopic, message);
            log.info("Successfully sent activity message to topic {}: {}", activityTopic, message);
        } catch (Exception e){
            log.error("Failed to send activity message to topic {}", activityTopic, e);
        }
    }
}
