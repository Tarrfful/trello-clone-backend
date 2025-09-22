package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.ActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityProducerService {

    private final KafkaTemplate<String, ActivityEvent> kafkaTemplate;

    @Value("${app.kafka.topic.activity}")
    private String activityTopic;

    public void sendActivityEvent(ActivityEvent event){
        try{
            kafkaTemplate.send(activityTopic, event);
            log.info("Successfully sent activity message to topic {}: {}", activityTopic, event);
        } catch (Exception e){
            log.error("Failed to send activity message to topic {}", activityTopic, e);
        }
    }
}
