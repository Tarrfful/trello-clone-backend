package com.tarfful.trello_clone.dto;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {

}
