package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/lists/{listId}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long listId,
            @RequestBody CreateTaskRequest request
            ){
        TaskResponse createdTask = taskService.createTask(listId, request);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }
}
