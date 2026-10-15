package com.nexscale.api.category.controller;

import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.service.CategoryService;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CategoryController.class, GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @DisplayName("GET /api/categories dapat diakses secara publik dan mengembalikan 200 OK beserta list kategori")
    void getAllCategories_publicAccess_returnsOkWithList() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<CategoryResponse> mockList = List.of(
                new CategoryResponse(id1, "Computer Vision", "Citra dan visual AI", Instant.now()),
                new CategoryResponse(id2, "Natural Language Processing", "NLP dan LLM", Instant.now())
        );

        when(categoryService.getAllCategories()).thenReturn(mockList);

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId", is(id1.toString())))
                .andExpect(jsonPath("$[0].name", is("Computer Vision")))
                .andExpect(jsonPath("$[1].categoryId", is(id2.toString())))
                .andExpect(jsonPath("$[1].name", is("Natural Language Processing")));
    }

    @Test
    @DisplayName("GET /api/categories/{id} dapat diakses secara publik dan mengembalikan 200 OK beserta detail kategori")
    void getCategoryById_publicAccess_returnsOk() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryResponse mockCategory = new CategoryResponse(
                categoryId,
                "Predictive Analytics",
                "Machine learning tabular",
                Instant.now()
        );

        when(categoryService.getCategoryById(categoryId)).thenReturn(mockCategory);

        mockMvc.perform(get("/api/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId", is(categoryId.toString())))
                .andExpect(jsonPath("$.name", is("Predictive Analytics")))
                .andExpect(jsonPath("$.description", is("Machine learning tabular")));
    }

    @Test
    @DisplayName("GET /api/categories/{id} ketika ID tidak ditemukan mengembalikan 404 Not Found")
    void getCategoryById_notFound_returnsNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        when(categoryService.getCategoryById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Kategori tidak ditemukan"));

        mockMvc.perform(get("/api/categories/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Data Tidak Ditemukan")))
                .andExpect(jsonPath("$.message", is("Kategori tidak ditemukan")));
    }
}
