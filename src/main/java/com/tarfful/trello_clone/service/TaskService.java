package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.AssigneeRequest;
import com.tarfful.trello_clone.dto.AssigneeResponse;
import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.MoveTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.dto.UpdateTaskRequest;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(Long listId, CreateTaskRequest request);

    List<TaskResponse> getAllTasks(Long listId);

    TaskResponse updateTask(Long taskId, UpdateTaskRequest request);

    void deleteTask(Long taskId);

    void moveTask(Long taskId, MoveTaskRequest request);

    TaskResponse assignUserToTask(Long taskId, AssigneeRequest request);

    TaskResponse unassignUserFromTask(Long taskId, Long assigneeId);
}
