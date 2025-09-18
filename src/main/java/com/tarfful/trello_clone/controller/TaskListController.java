package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;
import com.tarfful.trello_clone.service.TaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boards/{boardId}/lists")
@RequiredArgsConstructor
public class TaskListController {
    private final TaskListService taskListService;

    @PostMapping
    public ResponseEntity<TaskListResponse> createTaskList(
            @PathVariable Long boardId,
            @RequestBody CreateTaskListRequest request
            ){
        TaskListResponse createdList = taskListService.createTaskList(boardId, request);
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

}
