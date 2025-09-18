package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;
import com.tarfful.trello_clone.model.TaskList;

import java.util.List;

public interface TaskListService {
    TaskListResponse createTaskList(Long boardId, CreateTaskListRequest request);

    List<TaskListResponse> getAllTaskLists(Long boardId);
}
