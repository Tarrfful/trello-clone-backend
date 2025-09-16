package com.tarfful.trello_clone.dto;

public record UpdateBoardRequest(
        String name,
        String description
) {
}
