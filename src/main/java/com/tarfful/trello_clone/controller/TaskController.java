package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.AssigneeRequest;
import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.MoveTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.dto.UpdateTaskRequest;
import com.tarfful.trello_clone.model.Task;
import com.tarfful.trello_clone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(@PathVariable Long listId){
        List<TaskResponse> tasks = taskService.getAllTasks(listId);
        return ResponseEntity.ok(tasks);
    }
}

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
class SingleTaskController{

    private final TaskService taskService;

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request
            ){
        TaskResponse updatedTask = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<Void> moveTask(
            @PathVariable Long taskId,
            @RequestBody MoveTaskRequest request
            ){
        taskService.moveTask(taskId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/assignees")
    public ResponseEntity<TaskResponse> assignUserToTask(
            @PathVariable Long taskId,
            @RequestBody AssigneeRequest request
            ){
        TaskResponse updatedTask = taskService.assignUserToTask(taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}/assignees/{assigneeId}")
    public ResponseEntity<TaskResponse> unassignUserFromTask(
            @PathVariable Long taskId,
            @PathVariable Long assigneeId
    ){
        TaskResponse updatedTask = taskService.unassignUserFromTask(taskId, assigneeId);
        return ResponseEntity.ok(updatedTask);
    }
}

