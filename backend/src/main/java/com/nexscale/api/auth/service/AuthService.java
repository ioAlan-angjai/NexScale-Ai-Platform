package com.nexscale.api.auth.service;

import com.nexscale.api.auth.dto.LoginRequest;
import com.nexscale.api.auth.dto.LoginResponse;
import com.nexscale.api.common.exception.InvalidCredentialsException;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    public LoginResponse login(LoginRequest request) {
        // 1. Cari user berdasarkan email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email atau kata sandi tidak valid"));

        // 2. Verifikasi kecocokan password hash menggunakan BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email atau kata sandi tidak valid");
        }

        // 3. Generate token JWT
        String accessToken = jwtService.generateAccessToken(user);

        // 4. Return DTO response
        return new LoginResponse(
                accessToken,
                expirationMs,
                user.getUserId(),
                user.getEmail(),
                user.getRole(),
                user.getFullName()
        );
    }
}
