package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.CreateTaskListRequest;
import com.tarfful.trello_clone.dto.TaskListResponse;
import com.tarfful.trello_clone.exception.BoardNotFoundException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.TaskList;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.BoardRepository;
import com.tarfful.trello_clone.repository.TaskListRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.TaskListService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskListResponse createTaskList(Long boardId, CreateTaskListRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + boardId));

        if (board.getMembers().stream().noneMatch(member -> member.getId().equals(currentUser.getId()))){
            throw new UnauthorizedException("User is not a member of this board");
        }

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
}
