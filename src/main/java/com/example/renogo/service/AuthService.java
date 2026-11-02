package com.example.renogo.service;

import com.example.renogo.dto.AuthResponse;
import com.example.renogo.dto.LoginRequest;
import com.example.renogo.entity.User;
import com.example.renogo.repository.UserRepository;
import com.example.renogo.security.JwtService;
import com.example.renogo.security.PasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getUserId() == null || request.getPassword() == null) {
            throw invalidCredentials();
        }

        User user = userRepository.findByUserIdIgnoreCase(request.getUserId().trim())
                .orElseThrow(AuthService::invalidCredentials);
        if (!"active".equalsIgnoreCase(user.getStatus())
                || user.getRole() == null || user.getRole().isBlank()
                || !passwordService.matches(request.getPassword(), user.getPassword())) {
            throw invalidCredentials();
        }

        if (passwordService.isLegacyPassword(user.getPassword())) {
            user.setPassword(passwordService.hash(request.getPassword()));
            userRepository.save(user);
        }

        String token = jwtService.createToken(user);
        AuthResponse.AuthenticatedUser authenticatedUser =
                new AuthResponse.AuthenticatedUser(user.getUserId(), user.getName(), user.getRole());
        return new AuthResponse(token, jwtService.getExpirationSeconds(), authenticatedUser);
    }

    private static ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
}
