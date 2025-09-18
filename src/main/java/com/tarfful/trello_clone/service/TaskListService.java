package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;

public interface TaskListService {
    TaskListResponse createTaskList(Long boardId, CreateTaskListRequest request);
}
