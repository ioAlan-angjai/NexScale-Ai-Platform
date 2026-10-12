package com.nexscale.api.auth.service;

import com.nexscale.api.auth.dto.LoginRequest;
import com.nexscale.api.auth.dto.LoginResponse;
import com.nexscale.api.common.exception.InvalidCredentialsException;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, 3600000L);

        dummyUser = new User("client@nexscale.com", "$2a$10$hashedPassword", Role.CLIENT, "Client User");
        dummyUser.setUserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Login berhasil jika email terdaftar dan password cocok")
    void login_success_returnsLoginResponse() {
        LoginRequest request = new LoginRequest("client@nexscale.com", "password123");

        when(userRepository.findByEmail("client@nexscale.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("password123", dummyUser.getPasswordHash())).thenReturn(true);
        when(jwtService.generateAccessToken(dummyUser)).thenReturn("mocked.jwt.token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600000L, response.getExpiresIn());
        assertEquals(dummyUser.getUserId(), response.getUserId());
        assertEquals("client@nexscale.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("Client User", response.getFullName());

        verify(jwtService).generateAccessToken(dummyUser);
    }

    @Test
    @DisplayName("Login gagal jika email tidak ditemukan melempar InvalidCredentialsException")
    void login_emailNotFound_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("notfound@nexscale.com", "password123");

        when(userRepository.findByEmail("notfound@nexscale.com")).thenReturn(Optional.empty());

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Email atau kata sandi tidak valid", ex.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Login gagal jika password salah melempar InvalidCredentialsException")
    void login_wrongPassword_throwsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("client@nexscale.com", "wrongPassword");

        when(userRepository.findByEmail("client@nexscale.com")).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches("wrongPassword", dummyUser.getPasswordHash())).thenReturn(false);

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Email atau kata sandi tidak valid", ex.getMessage());
        verify(jwtService, never()).generateAccessToken(any());
    }
}
