package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.CommentResponse;
import com.tarfful.trello_clone.dto.CreateCommentRequest;
import com.tarfful.trello_clone.exception.TaskNotFoundException;
import com.tarfful.trello_clone.model.Comment;
import com.tarfful.trello_clone.model.Task;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.CommentRepository;
import com.tarfful.trello_clone.repository.TaskRepository;
import com.tarfful.trello_clone.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final TaskServiceImpl taskServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Override
    @Transactional
    public CommentResponse createComment(Long taskId, CreateCommentRequest request){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        taskServiceImpl.checkMembershipAndGetTaskList(task.getTaskList().getId());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userServiceImpl.findUserByUsername(username);

        Comment newComment = Comment.builder()
                .text(request.text())
                .task(task)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(newComment);

        return mapCommentToResponse(savedComment);
    }

    private CommentResponse mapCommentToResponse(Comment comment){
        CommentResponse.AuthorResponse authorResponse = new CommentResponse.AuthorResponse(
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername()
        );

        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt(),
                authorResponse
        );
    }
}
