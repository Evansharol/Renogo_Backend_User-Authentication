package com.example.renogo.service;

import com.example.renogo.dto.AuthResponse;
import com.example.renogo.dto.LoginRequest;
import com.example.renogo.entity.User;
import com.example.renogo.repository.UserRepository;
import com.example.renogo.security.JwtService;
import com.example.renogo.security.PasswordService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTests {

    private static final String JWT_SECRET = "local-test-secret-with-at-least-32-bytes";

    @Test
    void successfulLegacyLoginUpgradesPasswordAndReturnsJwt() {
        UserRepository repository = mock(UserRepository.class);
        PasswordService passwordService = new PasswordService();
        JwtService jwtService = new JwtService(JWT_SECRET, 3_600_000);
        AuthService authService = new AuthService(repository, passwordService, jwtService);

        User user = activeUserWithPassword("admin123");
        when(repository.findByUserIdIgnoreCase("a001")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserId("a001");
        request.setPassword("admin123");

        AuthResponse response = authService.login(request);

        assertEquals("A001", response.getUser().userId());
        assertEquals("admin", response.getUser().role());
        assertEquals(3600, response.getExpiresIn());
        assertNotEquals("admin123", user.getPassword());
        assertTrue(passwordService.matches("admin123", user.getPassword()));
        assertTrue(jwtService.parseToken(response.getAccessToken()).getSubject().equals("A001"));
        verify(repository).save(user);
    }

    @Test
    void inactiveAccountCannotLogIn() {
        UserRepository repository = mock(UserRepository.class);
        PasswordService passwordService = new PasswordService();
        AuthService authService = new AuthService(
                repository,
                passwordService,
                new JwtService(JWT_SECRET, 3_600_000));

        User user = activeUserWithPassword(passwordService.hash("admin123"));
        user.setStatus("Inactive");
        when(repository.findByUserIdIgnoreCase("A001")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserId("A001");
        request.setPassword("admin123");

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> authService.login(request));
    }

    @Test
    void dealerCanLogInWithLegacyDatabasePassword() {
        UserRepository repository = mock(UserRepository.class);
        PasswordService passwordService = new PasswordService();
        JwtService jwtService = new JwtService(JWT_SECRET, 3_600_000);
        AuthService authService = new AuthService(repository, passwordService, jwtService);

        User user = activeUserWithPassword("123");
        user.setUserId("D001");
        user.setName("Sofia Garcia");
        user.setRole("dealer");
        when(repository.findByUserIdIgnoreCase("D001")).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest();
        request.setUserId("D001");
        request.setPassword("123");

        AuthResponse response = authService.login(request);

        assertEquals("D001", response.getUser().userId());
        assertEquals("dealer", response.getUser().role());
        assertTrue(passwordService.matches("123", user.getPassword()));
        assertEquals("D001", jwtService.parseToken(response.getAccessToken()).getSubject());
        verify(repository).save(user);
    }

    private User activeUserWithPassword(String password) {
        User user = new User();
        user.setUserId("A001");
        user.setName("Amelia Martin");
        user.setRole("admin");
        user.setStatus("Active");
        user.setPassword(password);
        return user;
    }
}
