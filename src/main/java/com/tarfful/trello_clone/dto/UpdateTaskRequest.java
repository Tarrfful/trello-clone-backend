package com.tarfful.trello_clone.dto;

public record UpdateTaskRequest(
        String title,
        String description
) {
}
