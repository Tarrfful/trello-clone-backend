package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.JwtService;
import com.tarfful.trello_clone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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

    @Override
    public String loginUser(String usernameOrEmail, String password){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );

        String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        return jwtService.generateToken(username);
    }

    @Override
    public User findUserByUsername(String username){
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
