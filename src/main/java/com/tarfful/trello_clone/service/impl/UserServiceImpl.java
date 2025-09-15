package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String username, String email, String password){
        String hashedPassword = passwordEncoder.encode(password);

        User newUser = User.builder()
                .username(username)
                .email(email)
                .password(hashedPassword)
                .build();

        return userRepository.save(newUser);
    }
}
