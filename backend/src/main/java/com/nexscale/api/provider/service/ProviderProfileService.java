package com.nexscale.api.provider.service;

import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.provider.dto.ProviderProfileResponse;
import com.nexscale.api.provider.dto.UpdateProviderProfileRequest;
import com.nexscale.api.provider.entity.ProviderProfile;
import com.nexscale.api.provider.repository.ProviderProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer yang menangani logika bisnis pengelolaan data profil provider.
 */
@Service
public class ProviderProfileService {

    private final ProviderProfileRepository providerProfileRepository;

    public ProviderProfileService(ProviderProfileRepository providerProfileRepository) {
        this.providerProfileRepository = providerProfileRepository;
    }

    /**
     * Mengambil data profil provider berdasarkan ID user yang sedang login.
     *
     * @param userId UUID dari user yang diambil dari konteks JWT.
     * @return DTO respons profil provider.
     * @throws ResourceNotFoundException jika profil belum ditemukan.
     */
    @Transactional(readOnly = true)
    public ProviderProfileResponse getProfileByUserId(UUID userId) {
        ProviderProfile profile = providerProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil provider tidak ditemukan untuk user ID: " + userId));
        return ProviderProfileResponse.fromEntity(profile);
    }

    /**
     * Memperbarui detail profil keahlian, bio, pengalaman, dan status ketersediaan provider.
     * Menggunakan strategi fetch-then-update untuk menjamin integritas 1:1 tanpa risiko duplicate entry.
     *
     * @param userId  UUID dari user yang sedang login.
     * @param request data pembaruan yang telah tervalidasi.
     * @return DTO respons profil provider setelah diperbarui.
     */
    @Transactional
    public ProviderProfileResponse updateProfile(UUID userId, UpdateProviderProfileRequest request) {
        ProviderProfile profile = providerProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil provider tidak ditemukan untuk user ID: " + userId));

        // Memperbarui atribut profil
        profile.setProviderType(request.getProviderType());
        profile.setCompanyName(request.getCompanyName());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setAvailabilityStatus(request.getAvailabilityStatus());

        // Menyimpan entitas yang telah diperbarui
        ProviderProfile updatedProfile = providerProfileRepository.save(profile);
        return ProviderProfileResponse.fromEntity(updatedProfile);
    }
}

