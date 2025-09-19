package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;
import com.tarfful.trello_clone.exception.BoardNotFoundException;
import com.tarfful.trello_clone.exception.InvalidOperationException;
import com.tarfful.trello_clone.exception.TaskListNotFoundException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.TaskList;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.BoardRepository;
import com.tarfful.trello_clone.repository.TaskListRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.TaskListService;
import jakarta.persistence.Lob;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskListResponse createTaskList(Long boardId, CreateTaskListRequest request){
        Board board = getBoardOrThrow(boardId);

        checkMembership(currentUser(), board);

        int listOrder = taskListRepository.findMaxListOrderByBoardId(boardId)
                .map(maxOrder -> maxOrder + 1)
                .orElse(0);

        TaskList newTaskList = TaskList.builder()
                .name(request.name())
                .board(board)
                .listOrder(listOrder)
                .build();

        TaskList savedList = taskListRepository.save(newTaskList);

        return new TaskListResponse(savedList.getId(), savedList.getName(), savedList.getListOrder());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskListResponse> getAllTaskLists(Long boardId){
        checkMembership(currentUser(), getBoardOrThrow(boardId));

        List<TaskList> taskLists = taskListRepository.findByBoardIdOrderByListOrderAsc(boardId);

        return taskLists.stream()
                .map(list -> new TaskListResponse(list.getId(), list.getName(), list.getListOrder()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskListResponse updateTaskList(Long boardId, Long listId, CreateTaskListRequest request){
        checkMembership(currentUser(), getBoardOrThrow(boardId));

        TaskList taskList = getTaskListOrThrow(listId);

        if (!taskList.getBoard().getId().equals(boardId)){
            throw new InvalidOperationException("Task list does not belong to the specified board");
        }

        taskList.setName(request.name());
        TaskList updatedList = taskListRepository.save(taskList);

        return new TaskListResponse(updatedList.getId(), updatedList.getName(), updatedList.getListOrder());
    }

    private User currentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Board getBoardOrThrow(Long boardId){
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + boardId));
    }

    private void checkMembership(User user, Board board){
        if (board.getMembers().stream().noneMatch(member -> member.getId().equals(user.getId()))){
            throw new UnauthorizedException("User is not a member of this board");
        }
    }

    private TaskList getTaskListOrThrow(Long listId){
        return taskListRepository.findById(listId)
                .orElseThrow(() -> new TaskListNotFoundException("Task list not found with id: " + listId));
    }


}
