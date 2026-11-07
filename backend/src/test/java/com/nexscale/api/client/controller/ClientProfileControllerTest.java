package com.nexscale.api.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.client.dto.ClientProfileResponse;
import com.nexscale.api.client.dto.UpdateClientProfileRequest;
import com.nexscale.api.client.service.ClientProfileService;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebMvcTest untuk ClientProfileController.
 * Menguji keamanan akses berbasis role CLIENT dan validasi payload DTO.
 */
@WebMvcTest(controllers = {ClientProfileController.class, GlobalExceptionHandler.class})
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
@EnableMethodSecurity
class ClientProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientProfileService clientProfileService;

    @MockitoBean
    private JwtService jwtService;

    private final String clientToken = "valid.client.token";
    private final String providerToken = "valid.provider.token";
    private final UUID mockUserId = UUID.randomUUID();
    private final UUID mockClientId = UUID.randomUUID();

    private void setupClientAuth() {
        when(jwtService.isTokenValid(clientToken)).thenReturn(true);
        when(jwtService.extractUserId(clientToken)).thenReturn(mockUserId);
        when(jwtService.extractRole(clientToken)).thenReturn("CLIENT");
    }

    private void setupProviderAuth() {
        when(jwtService.isTokenValid(providerToken)).thenReturn(true);
        when(jwtService.extractUserId(providerToken)).thenReturn(mockUserId);
        when(jwtService.extractRole(providerToken)).thenReturn("PROVIDER");
    }

    @Test
    @DisplayName("GET /api/clients/me dengan role CLIENT berhasil mengembalikan 200 OK")
    void getMyProfile_roleClient_returnsOk() throws Exception {
        setupClientAuth();

        ClientProfileResponse response = new ClientProfileResponse(
                mockClientId, mockUserId, "PT NexScale", "Enterprise", "AI & ML",
                "Solusi AI Enterprise", "Jakarta", "https://nexscale.ai",
                Instant.now(), Instant.now()
        );

        when(clientProfileService.getProfileByUserId(mockUserId)).thenReturn(response);

        mockMvc.perform(get("/api/clients/me")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId", is(mockClientId.toString())))
                .andExpect(jsonPath("$.companyName", is("PT NexScale")))
                .andExpect(jsonPath("$.industry", is("AI & ML")));
    }

    @Test
    @DisplayName("GET /api/clients/me dengan role PROVIDER ditolak 403 Forbidden")
    void getMyProfile_roleProvider_returnsForbidden() throws Exception {
        setupProviderAuth();

        mockMvc.perform(get("/api/clients/me")
                        .header("Authorization", "Bearer " + providerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/clients/me dengan payload valid berhasil mengembalikan 200 OK")
    void updateMyProfile_validPayload_returnsOk() throws Exception {
        setupClientAuth();

        UpdateClientProfileRequest request = new UpdateClientProfileRequest(
                "PT Maju Jaya", "Startup", "Fintech",
                "Deskripsi Perusahaan", "Bandung", "https://majujaya.com"
        );

        ClientProfileResponse updatedResponse = new ClientProfileResponse(
                mockClientId, mockUserId, "PT Maju Jaya", "Startup", "Fintech",
                "Deskripsi Perusahaan", "Bandung", "https://majujaya.com",
                Instant.now(), Instant.now()
        );

        when(clientProfileService.updateProfile(eq(mockUserId), any(UpdateClientProfileRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/clients/me")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("PT Maju Jaya")))
                .andExpect(jsonPath("$.location", is("Bandung")));
    }

    @Test
    @DisplayName("PUT /api/clients/me dengan nama perusahaan melebihi 200 karakter mengembalikan 400 Bad Request")
    void updateMyProfile_companyNameTooLong_returnsBadRequest() throws Exception {
        setupClientAuth();

        UpdateClientProfileRequest invalidRequest = new UpdateClientProfileRequest();
        invalidRequest.setCompanyName("A".repeat(201));

        mockMvc.perform(put("/api/clients/me")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validasi Gagal")));
    }
}
