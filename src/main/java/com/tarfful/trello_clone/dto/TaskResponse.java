package com.tarfful.trello_clone.dto;

import java.util.Set;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Integer taskOrder,
        Set<AssigneeResponse> assignees
) {
}
