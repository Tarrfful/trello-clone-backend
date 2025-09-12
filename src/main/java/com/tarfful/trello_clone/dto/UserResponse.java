package com.tarfful.trello_clone.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {}
