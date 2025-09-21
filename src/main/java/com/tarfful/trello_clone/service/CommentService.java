package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.CommentResponse;
import com.tarfful.trello_clone.dto.CreateCommentRequest;

public interface CommentService {
    CommentResponse createComment(Long taskId, CreateCommentRequest request);
}
