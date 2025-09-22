package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.ActivityEvent;
import com.tarfful.trello_clone.model.Activity;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.ActivityRepository;
import com.tarfful.trello_clone.repository.BoardRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityConsumerService {

    private final ActivityRepository activityRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @KafkaListener(topics = "${app.kafka.topic.activity}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void listenActivityTopic(ActivityEvent event){
        log.info("Received activity event from Kafka topic: {}", event);
        try{
            Board board = boardRepository.findById(event.boardId())
                    .orElseThrow(() -> new RuntimeException("Board not found for activity log"));
            User user = userRepository.findById(event.userId())
                    .orElseThrow(() -> new RuntimeException("User not found for activity log"));

            Activity activity = Activity.builder()
                    .message(event.message())
                    .board(board)
                    .user(user)
                    .createdAt(event.timestamp())
                    .build();

            activityRepository.save(activity);
            log.info("Successfully saved activity log to database.");
        }catch (Exception e){
            log.error("Error processing activity event from Kafka: {}", event, e);
        }
    }
}
