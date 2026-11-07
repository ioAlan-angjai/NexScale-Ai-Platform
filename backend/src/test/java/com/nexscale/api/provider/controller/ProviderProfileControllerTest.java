package com.nexscale.api.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.provider.dto.ProviderProfileResponse;
import com.nexscale.api.provider.dto.UpdateProviderProfileRequest;
import com.nexscale.api.provider.service.ProviderProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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
 * WebMvcTest untuk ProviderProfileController.
 * Menguji otorisasi role PROVIDER dan penolakan terhadap role CLIENT.
 */
@WebMvcTest(controllers = {ProviderProfileController.class, GlobalExceptionHandler.class})
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
@EnableMethodSecurity
class ProviderProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProviderProfileService providerProfileService;

    @MockitoBean
    private JwtService jwtService;

    private final String providerToken = "valid.provider.token";
    private final String clientToken = "valid.client.token";
    private final UUID mockUserId = UUID.randomUUID();
    private final UUID mockProviderId = UUID.randomUUID();

    private void setupProviderAuth() {
        when(jwtService.isTokenValid(providerToken)).thenReturn(true);
        when(jwtService.extractUserId(providerToken)).thenReturn(mockUserId);
        when(jwtService.extractRole(providerToken)).thenReturn("PROVIDER");
    }

    private void setupClientAuth() {
        when(jwtService.isTokenValid(clientToken)).thenReturn(true);
        when(jwtService.extractUserId(clientToken)).thenReturn(mockUserId);
        when(jwtService.extractRole(clientToken)).thenReturn("CLIENT");
    }

    @Test
    @DisplayName("GET /api/providers/me dengan role PROVIDER berhasil mengembalikan 200 OK")
    void getMyProfile_roleProvider_returnsOk() throws Exception {
        setupProviderAuth();

        ProviderProfileResponse response = new ProviderProfileResponse(
                mockProviderId, mockUserId, "FREELANCER", "AI Dev Studio",
                "Senior Computer Vision Engineer", "Surabaya", 5,
                "AVAILABLE", "VERIFIED", new BigDecimal("4.95"),
                Instant.now(), Instant.now()
        );

        when(providerProfileService.getProfileByUserId(mockUserId)).thenReturn(response);

        mockMvc.perform(get("/api/providers/me")
                        .header("Authorization", "Bearer " + providerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerId", is(mockProviderId.toString())))
                .andExpect(jsonPath("$.providerType", is("FREELANCER")))
                .andExpect(jsonPath("$.experienceYears", is(5)));
    }

    @Test
    @DisplayName("GET /api/providers/me dengan role CLIENT ditolak 403 Forbidden")
    void getMyProfile_roleClient_returnsForbidden() throws Exception {
        setupClientAuth();

        mockMvc.perform(get("/api/providers/me")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/providers/me dengan payload valid berhasil mengembalikan 200 OK")
    void updateMyProfile_validPayload_returnsOk() throws Exception {
        setupProviderAuth();

        UpdateProviderProfileRequest request = new UpdateProviderProfileRequest(
                "AGENCY", "DataCore AI", "Spesialis NLP dan LLM Fine-Tuning",
                "Yogyakarta", 4, "BUSY"
        );

        ProviderProfileResponse updatedResponse = new ProviderProfileResponse(
                mockProviderId, mockUserId, "AGENCY", "DataCore AI", "Spesialis NLP dan LLM Fine-Tuning",
                "Yogyakarta", 4, "BUSY", "PENDING", new BigDecimal("5.00"),
                Instant.now(), Instant.now()
        );

        when(providerProfileService.updateProfile(eq(mockUserId), any(UpdateProviderProfileRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/providers/me")
                        .header("Authorization", "Bearer " + providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName", is("DataCore AI")))
                .andExpect(jsonPath("$.experienceYears", is(4)));
    }

    @Test
    @DisplayName("PUT /api/providers/me dengan experienceYears negatif mengembalikan 400 Bad Request")
    void updateMyProfile_negativeExperienceYears_returnsBadRequest() throws Exception {
        setupProviderAuth();

        UpdateProviderProfileRequest invalidRequest = new UpdateProviderProfileRequest();
        invalidRequest.setExperienceYears(-2);

        mockMvc.perform(put("/api/providers/me")
                        .header("Authorization", "Bearer " + providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validasi Gagal")));
    }
}
