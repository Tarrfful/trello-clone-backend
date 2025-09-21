package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.CommentResponse;
import com.tarfful.trello_clone.dto.CreateCommentRequest;
import com.tarfful.trello_clone.exception.CommentNotFoundException;
import com.tarfful.trello_clone.exception.TaskNotFoundException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
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

import java.util.List;
import java.util.stream.Collectors;

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

        User currentUser = taskServiceImpl.getCurrentUser();
        taskServiceImpl.getTaskListAndCheckMembership(task.getTaskList().getId(), currentUser);

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

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTaskId(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        User currentUser = taskServiceImpl.getCurrentUser();
        taskServiceImpl.getTaskListAndCheckMembership(task.getTaskList().getId(), currentUser);

        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);

        return comments.stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CreateCommentRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userServiceImpl.findUserByUsername(username);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("User is not the author of this comment");
        }

        comment.setText(request.text());
        Comment updatedComment = commentRepository.save(comment);

        return mapCommentToResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userServiceImpl.findUserByUsername(username);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("User is not the author of this comment");
        }

        commentRepository.delete(comment);
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
