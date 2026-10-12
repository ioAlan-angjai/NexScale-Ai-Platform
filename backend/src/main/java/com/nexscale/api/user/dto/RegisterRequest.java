package com.nexscale.api.user.dto;

import com.nexscale.api.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class RegisterRequest {

@NotBlank(message = "Email wajib diisi")
@Email(message = "Format email tidak valid")
@Size(max = 255, message = "Email maksimal 255 karakter")
private String email;

@NotBlank(message = "Password wajib diisi")
@Size(min = 8, max = 72, message = "Password harus 8 sampai 72 karakter")
private String password;

@NotNull(message = "Role wajib diisi")
private Role role;

@NotBlank(message = "Nama lengkap wajib diisi")
@Size(max = 150, message = "Nama lengkap maksimal 150 karakter")
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
