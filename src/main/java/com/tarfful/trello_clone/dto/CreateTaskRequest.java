package com.tarfful.trello_clone.dto;

public record CreateTaskRequest(
        String title,
        String description
) {
}
