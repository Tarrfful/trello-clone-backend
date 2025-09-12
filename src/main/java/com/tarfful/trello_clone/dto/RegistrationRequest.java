package com.tarfful.trello_clone.dto;

public record RegistrationRequest(
        String username,
        String email,
        String password
) {}
