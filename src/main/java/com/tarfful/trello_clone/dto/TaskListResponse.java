package com.tarfful.trello_clone.dto;

public record TaskListResponse(
        Long id,
        String name,
        Integer listOrder
) {
}
