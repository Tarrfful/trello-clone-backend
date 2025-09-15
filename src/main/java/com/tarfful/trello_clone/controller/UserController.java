package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.LoginRequest;
import com.tarfful.trello_clone.dto.LoginResponce;
import com.tarfful.trello_clone.dto.RegistrationRequest;
import com.tarfful.trello_clone.dto.UserResponse;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegistrationRequest request){
        User registeredUser = userService.registerUser(
                request.username(),
                request.email(),
                request.password()
        );

        UserResponse response = new UserResponse(
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getEmail()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponce> loginUser(@RequestBody LoginRequest request){
        String token = userService.loginUser(request.usernameOrEmail(), request.password());
        LoginResponce responce = new LoginResponce(token);
        return ResponseEntity.ok(responce);
    }
}
