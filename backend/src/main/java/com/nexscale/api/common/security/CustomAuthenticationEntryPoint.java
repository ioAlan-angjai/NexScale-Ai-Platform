package com.nexscale.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        // 1. Set status HTTP 401 Unauthorized dan Content-Type JSON
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 2. Susun payload error konsisten dengan ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Autentikasi Gagal",
                "Akses ditolak: Token autentikasi tidak valid atau tidak ditemukan",
                Instant.now().toString()
        );

        // 3. Tulis response JSON langsung ke output stream HTTP
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
