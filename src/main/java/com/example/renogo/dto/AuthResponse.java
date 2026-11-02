package com.example.renogo.dto;

public class AuthResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;
    private final AuthenticatedUser user;

    public AuthResponse(String accessToken, long expiresIn, AuthenticatedUser user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public record AuthenticatedUser(String userId, String name, String role) {
    }
}
