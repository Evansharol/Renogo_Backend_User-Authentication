package com.example.renogo.security;

import com.example.renogo.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtFilterTests {

    private static final String JWT_SECRET = "local-test-secret-with-at-least-32-bytes";

    @Test
    void missingTokenReturnsJson401WithoutBasicChallenge() throws Exception {
        JwtFilter filter = new JwtFilter(new JwtService(JWT_SECRET, 3_600_000), new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.setServletPath("/api/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, unusedChain());

        assertEquals(401, response.getStatus());
        assertNull(response.getHeader("WWW-Authenticate"));
        assertTrue(response.getContentAsString().contains("Unauthorized"));
    }

    @Test
    void validAdminTokenPassesToTheApi() throws Exception {
        JwtService jwtService = new JwtService(JWT_SECRET, 3_600_000);
        JwtFilter filter = new JwtFilter(jwtService, new ObjectMapper());
        User user = new User();
        user.setUserId("A001");
        user.setRole("admin");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");
        request.setServletPath("/api/users");
        request.addHeader("Authorization", "Bearer " + jwtService.createToken(user));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continued = new AtomicBoolean();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                continued.set(true));

        assertTrue(continued.get());
        assertEquals(200, response.getStatus());
        assertEquals("A001", request.getAttribute("authenticatedUserId"));
    }

    @Test
    void loginEndpointDoesNotRequireToken() throws Exception {
        JwtFilter filter = new JwtFilter(new JwtService(JWT_SECRET, 3_600_000), new ObjectMapper());
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setServletPath("/api/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continued = new AtomicBoolean();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                continued.set(true));

        assertTrue(continued.get());
        assertFalse(response.getStatus() == 401);
    }

    private FilterChain unusedChain() {
        return (request, response) -> {
            throw new IOException("Filter chain should not be called");
        };
    }
}
