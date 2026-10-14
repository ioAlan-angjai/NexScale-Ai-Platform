package com.nexscale.api.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        entryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    @DisplayName("commence mengembalikan HTTP 401 dengan format ErrorResponse JSON yang konsisten")
    void commence_shouldReturn401WithErrorResponseJson() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        entryPoint.commence(request, response, authException);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals(401, errorResponse.status());
        assertEquals("Autentikasi Gagal", errorResponse.error());
        assertEquals("Akses ditolak: Token autentikasi tidak valid atau tidak ditemukan", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
}
