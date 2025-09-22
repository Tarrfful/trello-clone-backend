package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.model.Activity;
import com.tarfful.trello_clone.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityConsumerService {

    private final ActivityRepository activityRepository;

    @KafkaListener(topics = "${app.kafka.topic.activity}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenActivityTopic(String message){
        log.info("Received message from Kafka topic: {}", message);
        try{
            Activity activity = Activity.builder()
                    .message(message)
                    .build();
            log.info("Activity log processed (simulation): {}", message);
        }catch (Exception e){
            log.error("Error processing activity message from Kafka: {}", message, e);
        }
    }
}
