package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.AuthResponse;
import com.saurabh.ExpenseTracker.dto.RegisterRequest;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                hashedPassword,
                "USER"
        );

        userRepository.save(user);

        // Generate JWT immediately after registration
        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                "User registered successfully"
        );
    }

    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid username or password"
            );
        }

        return jwtService.generateToken(user.getUsername());
    }
}