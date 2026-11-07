package com.nexscale.api.provider.controller;

import com.nexscale.api.provider.dto.ProviderProfileResponse;
import com.nexscale.api.provider.dto.UpdateProviderProfileRequest;
import com.nexscale.api.provider.service.ProviderProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller untuk mengelola profil provider AI (/api/providers/me).
 * Hanya dapat diakses oleh pengguna terautentikasi yang memiliki role 'PROVIDER'.
 */
@RestController
@RequestMapping("/api/providers/me")
@PreAuthorize("hasRole('PROVIDER')")
public class ProviderProfileController {

    private final ProviderProfileService providerProfileService;

    public ProviderProfileController(ProviderProfileService providerProfileService) {
        this.providerProfileService = providerProfileService;
    }

    /**
     * Endpoint untuk melihat profil provider milik user yang sedang login.
     */
    @GetMapping
    public ResponseEntity<ProviderProfileResponse> getMyProfile(@AuthenticationPrincipal UUID currentUserId) {
        ProviderProfileResponse response = providerProfileService.getProfileByUserId(currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint untuk memperbarui data keahlian, bio, dan status ketersediaan provider.
     */
    @PutMapping
    public ResponseEntity<ProviderProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UUID currentUserId,
            @Valid @RequestBody UpdateProviderProfileRequest request
    ) {
        ProviderProfileResponse response = providerProfileService.updateProfile(currentUserId, request);
        return ResponseEntity.ok(response);
    }
}

