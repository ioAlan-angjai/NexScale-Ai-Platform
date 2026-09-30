package com.nexscale.api.health.controller;

import com.nexscale.api.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test untuk HealthController menggunakan @WebMvcTest 
 * Tidak menjalankan server - MockMvc mensimulasikan HTTP Request secara in-memory
 */
@WebMvcTest(controllers = { 
    HealthController.class,
    GlobalExceptionHandler.class
})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/health harus mengembalikan 200 OK dengan status UP")
    void healthCheck_returnOkWithStatusUp() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.status", is("UP")))
            .andExpect(jsonPath("$.service", is("nexscale-ai")))
            .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET endpoint yang tidak ada harus mengembalikan 404 dalam format JSON")
    void unknownEndpoint_return404AsJson() throws Exception {
        mockMvc.perform(get("/api/tidak-ada"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", notNullValue()))
            .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}
