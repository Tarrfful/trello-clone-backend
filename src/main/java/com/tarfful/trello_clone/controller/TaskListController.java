package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;
import com.tarfful.trello_clone.service.TaskListService;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<TaskListResponse>> getAllTaskLists(@PathVariable Long boardId){
        List<TaskListResponse> taskLists = taskListService.getAllTaskLists(boardId);
        return ResponseEntity.ok(taskLists);
    }
    
    @PutMapping("/{listId}")
    public ResponseEntity<TaskListResponse> updateTaskList(
            @PathVariable Long boardId,
            @PathVariable Long listId,
            @RequestBody CreateTaskListRequest request
    ){
        TaskListResponse updatedList = taskListService.updateTaskList(boardId, listId, request);
        return ResponseEntity.ok(updatedList);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteTaskList(
            @PathVariable Long boardId,
            @PathVariable Long listId
    ){
        taskListService.deleteTaskList(boardId, listId);
        return ResponseEntity.noContent().build();
    }
}
