package com.tarfful.trello_clone.dto;

public record CreateBoardRequest(
        String name,
        String description
) {
}
