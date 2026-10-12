package com.nexscale.api.common.security;

import com.nexscale.api.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
        @Value("${security.jwt.secret-key}") String secret,
        @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    //1. Membuat access token dari user
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder().subject(user.getEmail())
        .claim("userId", user.getUserId().toString())
        .claim("role", user.getRole().name())
        .claim("fullName", user.getFullName())
        .issuedAt(now).expiration(expiryDate)
        .signWith(secretKey).compact();

    }

    //2. mengekstrak Claims(payload data) dari token
    public Claims extractClaims(String token) {
        return Jwts.parser()
        .verifyWith(secretKey).build()
        .parseSignedClaims(token).getPayload();
    }

    //3. Mengekstrak Email(subject)
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();

    }

    //4. Memvalidasi apakah token masih berlaku
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}