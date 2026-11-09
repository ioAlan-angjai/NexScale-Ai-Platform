package com.nexscale.api.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexscale.api.common.config.SecurityConfig;
import com.nexscale.api.common.exception.GlobalExceptionHandler;
import com.nexscale.api.common.security.CustomAccessDeniedHandler;
import com.nexscale.api.common.security.CustomAuthenticationEntryPoint;
import com.nexscale.api.common.security.JwtService;
import com.nexscale.api.project.dto.CreateProjectRequest;
import com.nexscale.api.project.dto.ProjectResponse;
import com.nexscale.api.project.entity.ProjectStatus;
import com.nexscale.api.project.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProjectController.class, GlobalExceptionHandler.class})
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
@EnableMethodSecurity
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private JwtService jwtService;

    private final String clientToken = "valid.client.token";
    private final String providerToken = "valid.provider.token";
    private final UUID userId = UUID.randomUUID();

    private void setupClientAuth() {
        when(jwtService.isTokenValid(clientToken)).thenReturn(true);
        when(jwtService.extractUserId(clientToken)).thenReturn(userId);
        when(jwtService.extractRole(clientToken)).thenReturn("CLIENT");
    }

    private void setupProviderAuth() {
        when(jwtService.isTokenValid(providerToken)).thenReturn(true);
        when(jwtService.extractUserId(providerToken)).thenReturn(userId);
        when(jwtService.extractRole(providerToken)).thenReturn("PROVIDER");
    }

    private ProjectResponse response(UUID projectId) {
        ProjectResponse response = new ProjectResponse();
        response.setProjectId(projectId);
        response.setTitle("Deteksi Objek");
        response.setStatus(ProjectStatus.DRAFT);
        return response;
    }

    @Test
    @DisplayName("GET /api/projects dapat diakses publik dan mengembalikan daftar proyek")
    void getOpenProjects_public_returnsOk() throws Exception {
        when(projectService.getOpenProjects()).thenReturn(List.of(response(UUID.randomUUID())));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Deteksi Objek")));
    }

    @Test
    @DisplayName("POST /api/projects tanpa token ditolak dengan 401")
    void createProject_withoutToken_returnsUnauthorized() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setCategoryId(UUID.randomUUID());
        request.setTitle("Proyek Baru");
        request.setBudgetMin(new BigDecimal("1000"));
        request.setBudgetMax(new BigDecimal("2000"));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/projects oleh CLIENT mengembalikan 201 Created")
    void createProject_client_returnsCreated() throws Exception {
        setupClientAuth();
        UUID projectId = UUID.randomUUID();
        when(projectService.createProject(eq(userId), any(CreateProjectRequest.class)))
                .thenReturn(response(projectId));

        CreateProjectRequest request = new CreateProjectRequest();
        request.setCategoryId(UUID.randomUUID());
        request.setTitle("Proyek Baru");

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId", is(projectId.toString())))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    @DisplayName("POST /api/projects oleh PROVIDER ditolak dengan 403")
    void createProject_provider_returnsForbidden() throws Exception {
        setupProviderAuth();
        CreateProjectRequest request = new CreateProjectRequest();
        request.setCategoryId(UUID.randomUUID());
        request.setTitle("Proyek Provider");

        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer " + providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/projects/me tanpa token ditolak dengan 401")
    void getMyProjects_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/projects/{id} oleh CLIENT mengembalikan 204")
    void deleteProject_client_returnsNoContent() throws Exception {
        setupClientAuth();

        mockMvc.perform(delete("/api/projects/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isNoContent());
    }
}
