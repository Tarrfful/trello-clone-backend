package com.tarfful.trello_clone.dto;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Integer taskOrder
) {
}
