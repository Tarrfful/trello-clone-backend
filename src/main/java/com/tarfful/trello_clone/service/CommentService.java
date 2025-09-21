package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.CommentResponse;
import com.tarfful.trello_clone.dto.CreateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long taskId, CreateCommentRequest request);

    List<CommentResponse> getCommentsByTaskId(Long taskId);

    CommentResponse updateComment(Long commentId, CreateCommentRequest request);

    void deleteComment(Long commentId);
}
