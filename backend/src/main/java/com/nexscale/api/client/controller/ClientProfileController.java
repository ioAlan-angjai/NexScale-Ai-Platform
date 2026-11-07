package com.nexscale.api.client.controller;

import com.nexscale.api.client.dto.ClientProfileResponse;
import com.nexscale.api.client.dto.UpdateClientProfileRequest;
import com.nexscale.api.client.service.ClientProfileService;
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
 * REST Controller untuk mengelola profil klien (/api/clients/me).
 * Hanya dapat diakses oleh pengguna terautentikasi yang memiliki role 'CLIENT'.
 */
@RestController
@RequestMapping("/api/clients/me")
@PreAuthorize("hasRole('CLIENT')")
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    public ClientProfileController(ClientProfileService clientProfileService) {
        this.clientProfileService = clientProfileService;
    }

    /**
     * Endpoint untuk melihat profil klien milik user yang sedang login.
     * Mengambil userId langsung dari principal JWT via @AuthenticationPrincipal.
     */
    @GetMapping
    public ResponseEntity<ClientProfileResponse> getMyProfile(@AuthenticationPrincipal UUID currentUserId) {
        ClientProfileResponse response = clientProfileService.getProfileByUserId(currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint untuk memperbarui informasi profil klien milik user yang sedang login.
     */
    @PutMapping
    public ResponseEntity<ClientProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UUID currentUserId,
            @Valid @RequestBody UpdateClientProfileRequest request
    ) {
        ClientProfileResponse response = clientProfileService.updateProfile(currentUserId, request);
        return ResponseEntity.ok(response);
    }
}

