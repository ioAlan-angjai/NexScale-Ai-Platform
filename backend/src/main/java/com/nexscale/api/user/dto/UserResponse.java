package com.nexscale.api.user.dto;

import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    private UUID userId;
    private String email;
    private Role role;
    private String fullName;
    private String avatarUrl;
    private boolean isVerified;
    private Instant createdAt;

    public UserResponse() {
    }

    public UserResponse(UUID userId, String email, Role role, String fullName, String avatarUrl, boolean isVerified, Instant createdAt) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.isVerified(),
                user.getCreatedAt()
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
