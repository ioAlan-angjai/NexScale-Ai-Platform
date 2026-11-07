package com.nexscale.api.client.repository;

import com.nexscale.api.client.entity.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface untuk entitas ClientProfile.
 * Meng-extend JpaRepository untuk menyediakan operasi CRUD standar (save, findById, delete, dll.).
 */
@Repository
public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    /**
     * Mencari profil klien berdasarkan UUID akun pengguna (user_id).
     * Format penamaan method 'findByUser_UserId':
     * - 'findByUser': navigasi ke atribut 'user' di dalam ClientProfile.
     * - '_UserId': navigasi ke atribut 'userId' di dalam entitas User.
     * Karakter underscore (_) digunakan sebagai penegas navigasi relasi bertingkat di Spring Data JPA.
     *
     * @param userId UUID dari pengguna pemilik profil.
     * @return Optional berisi ClientProfile jika ditemukan, atau Optional.empty() jika belum ada.
     */
    Optional<ClientProfile> findByUser_UserId(UUID userId);

    /**
     * Memeriksa apakah seorang user sudah memiliki profil klien terdaftar.
     * Mencegah percobaan pembuatan profil ganda.
     *
     * @param userId UUID pengguna yang diperiksa.
     * @return true jika sudah ada baris di client_profiles, false jika belum.
     */
    boolean existsByUser_UserId(UUID userId);
}

