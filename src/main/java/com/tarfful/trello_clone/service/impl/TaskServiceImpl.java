package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.exception.TaskListNotFoundException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.Task;
import com.tarfful.trello_clone.model.TaskList;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.TaskListRepository;
import com.tarfful.trello_clone.repository.TaskRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskResponse createTask(Long listId, CreateTaskRequest request){
        TaskList taskList = checkMembershipAndGetTaskList(listId);

        int taskOrder = taskRepository.findMaxTaskOrderByTaskListId(listId)
                .map(maxOrder -> maxOrder + 1)
                .orElse(0);

        Task newTask = Task.builder()
                .title(request.title())
                .description(request.description())
                .taskList(taskList)
                .taskOrder(taskOrder)
                .build();

        Task savedTask = taskRepository.save(newTask);

        return mapTaskToTaskResponse(savedTask);
    }

    private TaskList checkMembershipAndGetTaskList(Long listId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TaskList taskList = taskListRepository.findById(listId)
                .orElseThrow(() -> new TaskListNotFoundException("Task list not found with id: " + listId));

        Board board = taskList.getBoard();

        if (board.getMembers().stream().noneMatch(member -> member.getId().equals(currentUser.getId()))){
            throw new UnauthorizedException("User is not a member of the board this task list belongs to");
        }

        return taskList;
    }

    private TaskResponse mapTaskToTaskResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskOrder()
        );
    }
}
