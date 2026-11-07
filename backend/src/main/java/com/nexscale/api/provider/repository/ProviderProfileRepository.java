package com.nexscale.api.provider.repository;

import com.nexscale.api.provider.entity.ProviderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface untuk entitas ProviderProfile.
 * Menyediakan metode akses database standar Spring Data JPA untuk profil penyedia AI.
 */
@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {

    /**
     * Mencari profil provider berdasarkan UUID pengguna (user_id).
     *
     * @param userId UUID dari user yang terkait dengan profil provider.
     * @return Optional berisi ProviderProfile jika ditemukan, atau Optional.empty() jika belum ada.
     */
    Optional<ProviderProfile> findByUser_UserId(UUID userId);

    /**
     * Memeriksa apakah seorang user sudah memiliki data profil provider.
     *
     * @param userId UUID pengguna yang diperiksa.
     * @return true jika sudah ada, false jika belum.
     */
    boolean existsByUser_UserId(UUID userId);
}

