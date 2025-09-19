package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(Long listId, CreateTaskRequest request);

    List<TaskResponse> getAllTasks(Long listId);
}
