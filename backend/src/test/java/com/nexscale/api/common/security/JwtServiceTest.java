package com.nexscale.api.common.security;

import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET = "rahasia-kunci-jwt-nexscale-ai-minimal-32-karakter-keamanan-tinggi";
    private static final long EXPIRATION_MS = 3600000; // 1 jam

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_MS);

        testUser = new User("developer@nexscale.com", "hashedPassword", Role.CLIENT, "Developer NexScale");
        testUser.setUserId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Generate token berhasil menghasilkan JWT yang valid dan memuat claims yang tepat")
    void generateAccessToken_success() {
        String token = jwtService.generateAccessToken(testUser);

        assertNotNull(token);
        assertFalse(token.isBlank());

        // Verifikasi subject berisi UUID userId
        assertEquals(testUser.getUserId(), jwtService.extractUserId(token));

        // Verifikasi custom claim role
        assertEquals("CLIENT", jwtService.extractRole(token));

        // Verifikasi TIDAK ada claim fullName dan userId terpisah
        Claims claims = jwtService.extractClaims(token);
        assertNull(claims.get("fullName"));
        assertNull(claims.get("userId"));

        // Verifikasi validitas token
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    @DisplayName("Token yang dimanipulasi atau sembarang string harus terdeteksi tidak valid")
    void isTokenValid_tamperedToken_returnsFalse() {
        assertFalse(jwtService.isTokenValid("token.palsu.sembarangan"));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid(null));
    }

    @Test
    @DisplayName("Token yang sudah expired harus terdeteksi tidak valid")
    void isTokenValid_expiredToken_returnsFalse() {
        // Buat JwtService dengan expiration negatif (sudah kedaluwarsa)
        JwtService expiredJwtService = new JwtService(TEST_SECRET, -1000);
        String expiredToken = expiredJwtService.generateAccessToken(testUser);

        assertFalse(jwtService.isTokenValid(expiredToken));
    }
}
