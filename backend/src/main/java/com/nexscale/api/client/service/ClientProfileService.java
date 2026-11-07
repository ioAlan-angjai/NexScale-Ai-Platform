package com.nexscale.api.client.service;

import com.nexscale.api.client.dto.ClientProfileResponse;
import com.nexscale.api.client.dto.UpdateClientProfileRequest;
import com.nexscale.api.client.entity.ClientProfile;
import com.nexscale.api.client.repository.ClientProfileRepository;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer yang menangani logika bisnis pengelolaan data profil klien.
 */
@Service
public class ClientProfileService {

    private final ClientProfileRepository clientProfileRepository;

    public ClientProfileService(ClientProfileRepository clientProfileRepository) {
        this.clientProfileRepository = clientProfileRepository;
    }

    /**
     * Mengambil profil klien berdasarkan UUID pengguna yang sedang login.
     *
     * @param userId UUID dari user yang didapat dari JWT token.
     * @return DTO ClientProfileResponse yang siap ditampilkan ke pengguna.
     * @throws ResourceNotFoundException jika profil belum ditemukan.
     */
    @Transactional(readOnly = true)
    public ClientProfileResponse getProfileByUserId(UUID userId) {
        ClientProfile profile = clientProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil klien tidak ditemukan untuk user ID: " + userId));
        return ClientProfileResponse.fromEntity(profile);
    }

    /**
     * Memperbarui informasi bisnis/perusahaan pada profil klien.
     * Menggunakan strategi fetch-then-update sehingga mencegah pelanggaran constraint UNIQUE(user_id).
     *
     * @param userId  UUID dari user yang sedang login.
     * @param request data pembaruan profil yang telah lolos validasi.
     * @return DTO profil klien setelah berhasil diperbarui.
     */
    @Transactional
    public ClientProfileResponse updateProfile(UUID userId, UpdateClientProfileRequest request) {
        ClientProfile profile = clientProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil klien tidak ditemukan untuk user ID: " + userId));

        // Melakukan update field
        profile.setCompanyName(request.getCompanyName());
        profile.setBusinessType(request.getBusinessType());
        profile.setIndustry(request.getIndustry());
        profile.setDescription(request.getDescription());
        profile.setLocation(request.getLocation());
        profile.setWebsite(request.getWebsite());

        // Menyimpan perubahan ke database
        ClientProfile updatedProfile = clientProfileRepository.save(profile);
        return ClientProfileResponse.fromEntity(updatedProfile);
    }
}

