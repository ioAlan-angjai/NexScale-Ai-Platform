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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    @DisplayName("POST /api/users dengan email kosong mengembalikan 400 dan service tidak dipanggil")
    void registerUser_blankEmail_returnBadRequestAndServiceNotCalled() throws Exception {
        // Semua field valid KECUALI email, agar penyebab 400 hanya satu: email kosong
        RegisterRequest request = new RegisterRequest(
                "",
                "password123",
                Role.CLIENT,
                "Tio Alan"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Input yang tidak valid tidak boleh sampai ke lapisan service
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users dengan format email salah mengembalikan 400")
    void registerUser_invalidEmailFormat_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("bukan-email", "password123", Role.CLIENT, "Tio Alan"),
                "email");
    }

    @Test
    @DisplayName("POST /api/users dengan password 7 karakter mengembalikan 400")
    void registerUser_passwordTooShort_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("tioalan@example.com", "Rahsia1", Role.CLIENT, "Tio Alan"),
                "password");
    }

    @Test
    @DisplayName("POST /api/users dengan password 73 karakter mengembalikan 400")
    void registerUser_passwordTooLong_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("tioalan@example.com", "a".repeat(73), Role.CLIENT, "Tio Alan"),
                "password");
    }

    @Test
    @DisplayName("POST /api/users dengan nama lengkap kosong mengembalikan 400")
    void registerUser_blankFullName_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("tioalan@example.com", "password123", Role.CLIENT, "   "),
                "fullName");
    }

    @Test
    @DisplayName("POST /api/users dengan nama lengkap 151 karakter mengembalikan 400")
    void registerUser_fullNameTooLong_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("tioalan@example.com", "password123", Role.CLIENT, "a".repeat(151)),
                "fullName");
    }

    @Test
    @DisplayName("POST /api/users tanpa role mengembalikan 400")
    void registerUser_nullRole_returnBadRequest() throws Exception {
        assertRegistrationRejected(
                new RegisterRequest("tioalan@example.com", "password123", null, "Tio Alan"),
                "role");
    }

    @Test
    @DisplayName("Respons error 400 tidak boleh memuat password yang dikirim klien")
    void registerUser_invalidInput_responseDoesNotLeakPassword() throws Exception {
        String submittedPassword = "Rahsia1"; // 7 karakter, sengaja terlalu pendek
        RegisterRequest request = new RegisterRequest(
                "tioalan@example.com", submittedPassword, Role.CLIENT, "Tio Alan");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString(submittedPassword))));
    }

    @Test
    @DisplayName("POST /api/users dengan JSON rusak mengembalikan 400 dan service tidak dipanggil")
    void registerUser_malformedJson_returnBadRequestAndServiceNotCalled() throws Exception {
        // Body sengaja dipotong sehingga bukan JSON yang valid
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users dengan role tidak dikenal mengembalikan 400 dan service tidak dipanggil")
    void registerUser_unknownRole_returnBadRequestAndServiceNotCalled() throws Exception {
        // Ditulis sebagai JSON mentah karena nilai "SUPERUSER" tidak bisa dibuat lewat enum Role
        String json = """
                {
                  "email": "tioalan@example.com",
                  "password": "password123",
                  "role": "SUPERUSER",
                  "fullName": "Tio Alan"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    /**
     * Helper: request harus ditolak 400 dengan format ErrorResponse yang benar,
     * pesannya menyebut field yang salah, dan UserService tidak boleh dipanggil.
     */
    private void assertRegistrationRejected(RegisterRequest request, String expectedFieldInMessage) throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validasi Gagal")))
                .andExpect(jsonPath("$.message", containsString(expectedFieldInMessage)));

        verifyNoInteractions(userService);
    }
}
