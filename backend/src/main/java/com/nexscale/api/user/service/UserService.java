package com.nexscale.api.user.service;

import com.nexscale.api.common.exception.DuplicateResourceException;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexscale.api.common.exception.RoleNotAllowedException;
import com.nexscale.api.user.entity.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;


@Service
public class UserService {
    //1. Deklarasi field repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //2. Constructor Injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        //0. Validasi aturan bisnis : role admin tidak boleh didaftarkan secara pinlik
        if (request.getRole() == Role.ADMIN) {
            throw new RoleNotAllowedException("Registrasi publik untuk role ADMIN tidak diizinkan");
        }
        //1. validasi aturan bisnis: Email yang sudah terdaftar tidak boleh mendaftar lagi
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email sudah terdaftar: " + request.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        //2.Transformasi DTO Request ke Entity Database
        User user = new User(request.getEmail(), encodedPassword, request.getRole(), request.getFullName());
        //3. simpan entity ke database via UserRepository
        User savedUser = userRepository.save(user);
        //4.Return respon dalam bentuk DTO aman(tanpa password)
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna tidak ditemukan"));
        return UserResponse.fromEntity(user);
    }
}
