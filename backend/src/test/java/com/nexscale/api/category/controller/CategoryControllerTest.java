package com.nexscale.api.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.category.dto.CategoryRequest;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.service.CategoryService;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.DuplicateResourceException;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CategoryController.class, GlobalExceptionHandler.class})
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
@EnableMethodSecurity
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    // ===== PUBLIC READ ENDPOINTS =====

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

    // ===== ADMIN CREATE ENDPOINT =====

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories dengan role ADMIN berhasil membuat kategori baru dan mengembalikan 201 Created")
    void createCategory_asAdmin_returnsCreated() throws Exception {
        CategoryRequest request = new CategoryRequest("Generative AI", "Proyek generatif berbasis AI");
        UUID newId = UUID.randomUUID();
        CategoryResponse mockResponse = new CategoryResponse(newId, "Generative AI", "Proyek generatif berbasis AI", Instant.now());

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId", is(newId.toString())))
                .andExpect(jsonPath("$.name", is("Generative AI")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories dengan nama duplikat mengembalikan 409 Conflict")
    void createCategory_duplicateName_returnsConflict() throws Exception {
        CategoryRequest request = new CategoryRequest("Computer Vision", "Duplikat");

        when(categoryService.createCategory(any(CategoryRequest.class)))
                .thenThrow(new DuplicateResourceException("Kategori dengan nama 'Computer Vision' sudah terdaftar"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Data Duplikat")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/categories dengan nama kosong mengembalikan 400 Bad Request")
    void createCategory_blankName_returnsBadRequest() throws Exception {
        CategoryRequest request = new CategoryRequest("", "Desc");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /api/categories dengan role CLIENT mengembalikan 403 Forbidden")
    void createCategory_asClient_returnsForbidden() throws Exception {
        CategoryRequest request = new CategoryRequest("New Category", "Desc");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ===== ADMIN UPDATE ENDPOINT =====

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/categories/{id} dengan role ADMIN berhasil memperbarui kategori dan mengembalikan 200 OK")
    void updateCategory_asAdmin_returnsOk() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("Updated Name", "Updated Desc");
        CategoryResponse mockResponse = new CategoryResponse(categoryId, "Updated Name", "Updated Desc", Instant.now());

        when(categoryService.updateCategory(eq(categoryId), any(CategoryRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId", is(categoryId.toString())))
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("PUT /api/categories/{id} dengan role CLIENT mengembalikan 403 Forbidden")
    void updateCategory_asClient_returnsForbidden() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("New Name", "Desc");

        mockMvc.perform(put("/api/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ===== ADMIN DELETE ENDPOINT =====

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/categories/{id} dengan role ADMIN berhasil menghapus dan mengembalikan 204 No Content")
    void deleteCategory_asAdmin_returnsNoContent() throws Exception {
        UUID categoryId = UUID.randomUUID();

        doNothing().when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("DELETE /api/categories/{id} dengan role CLIENT mengembalikan 403 Forbidden")
    void deleteCategory_asClient_returnsForbidden() throws Exception {
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isForbidden());
    }
}
