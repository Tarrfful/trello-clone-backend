package com.tarfful.trello_clone.dto;

public record MoveTaskRequest(
        Long newListId,
        Integer newPosition
) {
}
