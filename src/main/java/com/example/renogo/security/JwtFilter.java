package com.example.renogo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                || ("POST".equalsIgnoreCase(request.getMethod())
                && "/api/auth/login".equals(request.getServletPath()))
                || !request.getServletPath().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.length() <= "Bearer ".length()) {
            writeUnauthorized(response);
            return;
        }

        try {
            Claims claims = jwtService.parseToken(
                    authorization.substring("Bearer ".length()).trim());
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            if (userId == null || role == null) {
                writeUnauthorized(response);
                return;
            }
            if (request.getServletPath().startsWith("/api/users")
                    && !isAdministrator(role)) {
                writeForbidden(response);
                return;
            }
            request.setAttribute("authenticatedUserId", userId);
            request.setAttribute("authenticatedRole", role);
        } catch (JwtException | IllegalArgumentException exception) {
            writeUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAdministrator(String role) {
        String normalizedRole = role.replace("ROLE_", "").toLowerCase(Locale.ROOT);
        return normalizedRole.equals("admin") || normalizedRole.equals("administrator");
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), Map.of("error", "Unauthorized"));
    }

    private void writeForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), Map.of("error", "Forbidden"));
    }
}
