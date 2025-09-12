package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.model.User;

public interface UserService {
    User registerUser(String username, String email, String password);
}
