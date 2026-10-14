package com.nexscale.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        // 1. Set status HTTP 403 Forbidden dan Content-Type JSON
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 2. Susun payload error konsisten dengan ErrorResponse
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Akses Ditolak",
                "Anda tidak memiliki izin untuk mengakses resource ini",
                Instant.now().toString()
        );

        // 3. Tulis response JSON langsung ke output stream HTTP
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
