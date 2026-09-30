package com.nexscale.api.user.dto;

import com.nexscale.api.user.entity.Role;

public class RegisterRequest {

    private String email;
    private String password;
    private Role role;
    private String fullName;

    public RegisterRequest() {
    }

    public RegisterRequest(String email, String password, Role role, String fullName) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
