package com.nexscale.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.exception.DuplicateResourceException;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Memalsukan (Mock) UserService agar test fokus pada Controller layer saja
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users berhasil mengembalikan 201 Created")
    void registerUser_success_returnCreated() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "tioalan@example.com",
                "password123",
                Role.CLIENT,
                "Tio Alan"
        );

        UserResponse mockResponse = new UserResponse(
                UUID.randomUUID(),
                "tioalan@example.com",
                Role.CLIENT,
                "Tio Alan",
                null,
                false,
                Instant.now()
        );

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("tioalan@example.com")))
                .andExpect(jsonPath("$.fullName", is("Tio Alan")))
                .andExpect(jsonPath("$.role", is("CLIENT")));
    }

    @Test
    @DisplayName("POST /api/users gagal jika email duplikat mengembalikan 409 Conflict")
    void registerUser_duplicateEmail_returnConflict() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "password123",
                Role.CLIENT,
                "Existing User"
        );

        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("Email sudah terdaftar: existing@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("Email sudah terdaftar: existing@example.com")));
    }
}
