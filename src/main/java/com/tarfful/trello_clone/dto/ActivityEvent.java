package com.tarfful.trello_clone.dto;

import java.time.LocalDateTime;

public record ActivityEvent(
        String message,
        Long boardId,
        Long userId,
        LocalDateTime timestamp
) {
}
