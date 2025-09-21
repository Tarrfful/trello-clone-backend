package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.AssigneeRequest;
import com.tarfful.trello_clone.dto.AssigneeResponse;
import com.tarfful.trello_clone.dto.CreateTaskRequest;
import com.tarfful.trello_clone.dto.MoveTaskRequest;
import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.dto.UpdateTaskRequest;
import com.tarfful.trello_clone.exception.InvalidOperationException;
import com.tarfful.trello_clone.exception.TaskListNotFoundException;
import com.tarfful.trello_clone.exception.TaskNotFoundException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.Task;
import com.tarfful.trello_clone.model.TaskList;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.TaskListRepository;
import com.tarfful.trello_clone.repository.TaskRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.ActivityProducerService;
import com.tarfful.trello_clone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;
    private final ActivityProducerService activityProducerService;

    @Override
    @Transactional
    public TaskResponse createTask(Long listId, CreateTaskRequest request){
        User currentUser = getCurrentUser();

        TaskList taskList = getTaskListAndCheckMembership(listId, currentUser);

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

        String activityMessage = String.format(
                "User '%s' created task '%s' in list '%s'",
                currentUser.getUsername(),
                savedTask.getTitle(),
                taskList.getName()
        );

        activityProducerService.sendActivityMessage(activityMessage);

        return mapTaskToTaskResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(Long listId){
        User currentUser = getCurrentUser();

        TaskList taskList = getTaskListAndCheckMembership(listId, currentUser);

        List<Task> tasks = taskRepository.findByTaskListIdOrderByTaskOrderAsc(listId);

        return tasks.stream().map(this::mapTaskToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request){
        User currentUser = getCurrentUser();

        Task taskToUpdate = getTaskOrThrow(taskId);

        getTaskListAndCheckMembership(taskToUpdate.getTaskList().getId(), currentUser);

        taskToUpdate.setTitle(request.title());
        taskToUpdate.setDescription(request.description());

        Task updatedTask = taskRepository.save(taskToUpdate);
        return mapTaskToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId){
        User currentUser = getCurrentUser();

        Task taskToDelete = getTaskOrThrow(taskId);

        getTaskListAndCheckMembership(taskToDelete.getTaskList().getId(), currentUser);

        taskRepository.delete(taskToDelete);
    }

    @Override
    @Transactional
    public void moveTask(Long taskId, MoveTaskRequest request){
        User currentUser = getCurrentUser();

        Task taskToMove = getTaskOrThrow(taskId);
        TaskList sourceList = taskToMove.getTaskList();

        getTaskListAndCheckMembership(sourceList.getId(), currentUser);

        TaskList destinationList = getTaskListOrThrow(request.newListId());

        if (!sourceList.getBoard().getId().equals(destinationList.getBoard().getId())){
            throw new InvalidOperationException("Cannot move tasks between different boards");
        }

        long tasksInDestinationList = taskRepository.countByTaskListId(destinationList.getId());

        boolean isSameList = sourceList.getId().equals(destinationList.getId());

        long maxPosition = isSameList ? tasksInDestinationList - 1 : tasksInDestinationList;

        if (request.newPosition() < 0 || request.newPosition() > maxPosition){
            throw new InvalidOperationException("Invalid new position for the task. Position must be between 0 and " + maxPosition);
        }

        taskRepository.shiftOrdersUp(sourceList.getId(), taskToMove.getTaskOrder());
        taskRepository.shiftOrdersDown(destinationList.getId(), request.newPosition());

        taskToMove.setTaskList(destinationList);
        taskToMove.setTaskOrder(request.newPosition());

        taskRepository.save(taskToMove);
    }

    @Override
    @Transactional
    public TaskResponse assignUserToTask(Long taskId, AssigneeRequest request){
        User currentUser = getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        TaskList taskList = getTaskListAndCheckMembership(task.getTaskList().getId(), currentUser);

        User userToAssign = userRepository.findById(request.userId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + request.userId()));

        Board board = taskList.getBoard();
        if (board.getMembers().stream().noneMatch(member -> member.getId().equals(userToAssign.getId()))){
            throw new InvalidOperationException("Cannot assign a user who is not a member of the board");
        }

        task.getAssignees().add(userToAssign);
        Task updatedTask = taskRepository.save(task);

        return mapTaskToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public TaskResponse unassignUserFromTask(Long taskId, Long assigneeId){
        User currentUser = getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        getTaskListAndCheckMembership(task.getTaskList().getId(), currentUser);

        task.getAssignees().removeIf(user -> user.getId().equals(assigneeId));

        Task updatedTask = taskRepository.save(task);

        return mapTaskToTaskResponse(updatedTask);
    }


    User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    TaskList getTaskListAndCheckMembership(Long listId, User user){
        TaskList taskList = taskListRepository.findById(listId)
                .orElseThrow(() -> new TaskListNotFoundException("Task list not found with id: " + listId));

        Board board = taskList.getBoard();
        User currentUser = getCurrentUser();

        if (board.getMembers().stream().noneMatch(member -> member.getId().equals(currentUser.getId()))){
            throw new UnauthorizedException("User is not a member of the board this task list belongs to");
        }

        return taskList;
    }

    private TaskResponse mapTaskToTaskResponse(Task task){
        Set<AssigneeResponse> assigneeResponses = task.getAssignees().stream()
                .map(user -> new AssigneeResponse(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskOrder(),
                assigneeResponses
        );
    }

    private Task getTaskOrThrow(Long taskId){
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
    }

    private TaskList getTaskListOrThrow(Long listId){
        return taskListRepository.findById(listId)
                .orElseThrow(() -> new TaskListNotFoundException("Task list not found with id: " + listId));
    }
}
