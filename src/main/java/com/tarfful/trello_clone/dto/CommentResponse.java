package com.tarfful.trello_clone.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String text,
        LocalDateTime createdAt,
        AuthorResponse author
) {
    public record AuthorResponse(Long id, String username) {}
}
