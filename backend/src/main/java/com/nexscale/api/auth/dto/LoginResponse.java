package com.nexscale.api.auth.dto;

import com.nexscale.api.user.entity.Role;
import java.util.UUID;

public class LoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private String email;
    private Role role;
    private String fullName;
    private UUID userId;

    public LoginResponse() {

    }

    public LoginResponse(String accessToken, long expiresIn, UUID userId, String email, Role role, String fullName) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
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
    public UUID getUserId() {
        return userId;
    }
    public String getEmail() {
        return email;
    }
    public Role getRole() {
        return role;
    }
    public String getFullName() {
        return fullName;
    }
    

    
}