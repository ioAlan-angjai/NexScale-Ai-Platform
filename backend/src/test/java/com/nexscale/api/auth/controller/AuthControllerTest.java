package com.nexscale.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.auth.dto.LoginRequest;
import com.nexscale.api.auth.dto.LoginResponse;
import com.nexscale.api.auth.service.AuthService;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.exception.InvalidCredentialsException;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock AuthService agar test fokus pada controller layer
    @MockitoBean
    private AuthService authService;

    // Mock dependensi SecurityConfig
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("POST /api/auth/login berhasil mengembalikan 200 OK dengan token")
    void login_success_returnsOkWithToken() throws Exception {
        LoginRequest request = new LoginRequest("client@nexscale.com", "password123");

        LoginResponse mockResponse = new LoginResponse(
                "mocked.jwt.token",
                3600L,
                UUID.randomUUID(),
                "client@nexscale.com",
                Role.CLIENT,
                "Client User"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("mocked.jwt.token")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.expiresIn", is(3600)))
                .andExpect(jsonPath("$.role", is("CLIENT")));
    }

    @Test
    @DisplayName("POST /api/auth/login dengan kredensial salah mengembalikan 401")
    void login_invalidCredentials_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("wrong@nexscale.com", "wrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Email atau kata sandi tidak valid"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", is("Email atau kata sandi tidak valid")));
    }

    @Test
    @DisplayName("POST /api/auth/login dengan email kosong mengembalikan 400")
    void login_emptyEmail_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/auth/login dengan password kosong mengembalikan 400")
    void login_emptyPassword_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("client@nexscale.com", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("POST /api/auth/login dengan body JSON rusak mengembalikan 400")
    void login_malformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{email: broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));

        verifyNoInteractions(authService);
    }
}
