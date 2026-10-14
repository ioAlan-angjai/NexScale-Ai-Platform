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
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

class CustomAccessDeniedHandlerTest {

    private CustomAccessDeniedHandler accessDeniedHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        accessDeniedHandler = new CustomAccessDeniedHandler(objectMapper);
    }

    @Test
    @DisplayName("handle mengembalikan HTTP 403 dengan format ErrorResponse JSON yang konsisten")
    void handle_shouldReturn403WithErrorResponseJson() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = new AccessDeniedException("Access is denied");

        accessDeniedHandler.handle(request, response, accessDeniedException);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ErrorResponse errorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertNotNull(errorResponse);
        assertEquals(403, errorResponse.status());
        assertEquals("Akses Ditolak", errorResponse.error());
        assertEquals("Anda tidak memiliki izin untuk mengakses resource ini", errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
}
