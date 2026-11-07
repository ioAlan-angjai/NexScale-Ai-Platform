package com.nexscale.api.user.service;

import com.nexscale.api.client.entity.ClientProfile;
import com.nexscale.api.client.repository.ClientProfileRepository;
import com.nexscale.api.common.exception.DuplicateResourceException;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.common.exception.RoleNotAllowedException;
import com.nexscale.api.provider.entity.ProviderProfile;
import com.nexscale.api.provider.repository.ProviderProfileRepository;
import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer yang bertanggung jawab atas proses pendaftaran (registrasi) dan pembacaan user.
 * Mengotomatiskan pembuatan profil awal (auto-provisioning) untuk Client dan Provider dalam satu transaksi atomik.
 */
@Service
public class UserService {

    // 1. Deklarasi field dependensi (prinsip Immutability melalui final)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientProfileRepository clientProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;

    // 2. Constructor Injection (Praktik terbaik Spring Boot, tanpa @Autowired field)
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ClientProfileRepository clientProfileRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientProfileRepository = clientProfileRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    /**
     * Mendaftarkan pengguna baru ke platform.
     * Anotasi @Transactional memastikan bahwa operasi penyimpanan entitas User
     * dan pembuatan stub profil (ClientProfile atau ProviderProfile) berada dalam satu transaksi database.
     * Jika terjadi kegagalan di salah satu langkah, seluruh transaksi akan di-rollback (All-or-Nothing).
     *
     * @param request DTO input registrasi yang telah divalidasi.
     * @return UserResponse DTO aman tanpa field password.
     */
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // 0. Validasi aturan bisnis: Role ADMIN tidak boleh didaftarkan secara publik
        if (request.getRole() == Role.ADMIN) {
            throw new RoleNotAllowedException("Registrasi publik untuk role ADMIN tidak diizinkan");
        }

        // 1. Validasi aturan bisnis: Email yang sudah terdaftar tidak boleh mendaftar lagi
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email sudah terdaftar: " + request.getEmail());
        }

        // 2. Enkripsi password menggunakan BCrypt sebelum disimpan ke database
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Transformasi DTO Request ke Entity User
        User user = new User(request.getEmail(), encodedPassword, request.getRole(), request.getFullName());
        User savedUser = userRepository.save(user);

        // 4. Auto-provisioning stub profil sesuai Role pengguna dalam transaksi yang sama.
        // Langkah ini menjamin bahwa setiap akun Client atau Provider pasti memiliki baris profil
        // yang valid di database sejak pertama kali terdaftar.
        if (savedUser.getRole() == Role.CLIENT) {
            clientProfileRepository.save(new ClientProfile(savedUser));
        } else if (savedUser.getRole() == Role.PROVIDER) {
            providerProfileRepository.save(new ProviderProfile(savedUser));
        }

        // 5. Kembalikan respon dalam bentuk DTO aman (tanpa password hash)
        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Mengambil informasi user berdasarkan UUID.
     *
     * @param userId UUID pengguna.
     * @return UserResponse DTO.
     * @throws ResourceNotFoundException jika ID tidak ditemukan di tabel users.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));
        return UserResponse.fromEntity(user);
    }
}
