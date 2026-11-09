package com.nexscale.api.project.service;

import com.nexscale.api.category.entity.Category;
import com.nexscale.api.category.repository.CategoryRepository;
import com.nexscale.api.client.entity.ClientProfile;
import com.nexscale.api.client.repository.ClientProfileRepository;
import com.nexscale.api.common.exception.BadRequestException;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.common.exception.RoleNotAllowedException;
import com.nexscale.api.project.dto.CreateProjectRequest;
import com.nexscale.api.project.dto.ProjectResponse;
import com.nexscale.api.project.dto.UpdateProjectRequest;
import com.nexscale.api.project.entity.Project;
import com.nexscale.api.project.entity.ProjectStatus;
import com.nexscale.api.project.repository.ProjectRepository;
import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ClientProfileRepository clientProfileRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProjectService projectService;

    private User user(UUID userId) {
        User user = new User("client@nexscale.ai", "hash", Role.CLIENT, "Klien");
        user.setUserId(userId);
        return user;
    }

    private ClientProfile client(User user, UUID clientId) {
        ClientProfile client = new ClientProfile(user);
        client.setClientId(clientId);
        return client;
    }

    private Category category(UUID categoryId) {
        Category category = new Category("Computer Vision", "Citra AI");
        category.setCategoryId(categoryId);
        return category;
    }

    private CreateProjectRequest request(UUID categoryId) {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setCategoryId(categoryId);
        request.setTitle("  Deteksi Objek  ");
        request.setDescription("Sistem deteksi objek gudang");
        request.setBudgetMin(new BigDecimal("1000"));
        request.setBudgetMax(new BigDecimal("5000"));
        request.setDurationEstimate(30);
        return request;
    }

    @Test
    @DisplayName("createProject menyimpan proyek berstatus DRAFT milik klien yang login")
    void createProject_success_savesDraft() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ClientProfile client = client(user(userId), UUID.randomUUID());
        Category category = category(categoryId);

        when(clientProfileRepository.findByUser_UserId(userId)).thenReturn(Optional.of(client));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.createProject(userId, request(categoryId));

        assertEquals("Deteksi Objek", response.getTitle());
        assertEquals(ProjectStatus.DRAFT, response.getStatus());
        assertEquals(categoryId, response.getCategoryId());
        assertEquals(client.getClientId(), response.getClientId());
    }

    @Test
    @DisplayName("createProject menolak budget maksimum yang lebih kecil dari minimum")
    void createProject_invalidBudget_throwsBadRequest() {
        CreateProjectRequest request = request(UUID.randomUUID());
        request.setBudgetMin(new BigDecimal("9000"));
        request.setBudgetMax(new BigDecimal("1000"));

        assertThrows(BadRequestException.class, () -> projectService.createProject(UUID.randomUUID(), request));
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("getOpenProjects hanya mengembalikan proyek berstatus OPEN")
    void getOpenProjects_returnsOnlyOpen() {
        Project project = new Project(client(user(UUID.randomUUID()), UUID.randomUUID()),
                category(UUID.randomUUID()), "Proyek Terbuka", null, null, null, null, null);
        project.setStatus(ProjectStatus.OPEN);
        when(projectRepository.findByStatus(ProjectStatus.OPEN)).thenReturn(List.of(project));

        List<ProjectResponse> result = projectService.getOpenProjects();

        assertEquals(1, result.size());
        assertEquals(ProjectStatus.OPEN, result.get(0).getStatus());
    }

    @Test
    @DisplayName("getProjectById menyembunyikan proyek DRAFT dari pengguna yang bukan pemilik")
    void getProjectById_draftForOtherUser_throwsNotFound() {
        UUID ownerId = UUID.randomUUID();
        Project project = new Project(client(user(ownerId), UUID.randomUUID()),
                category(UUID.randomUUID()), "Draft Rahasia", null, null, null, null, null);
        project.setProjectId(UUID.randomUUID());
        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectById(project.getProjectId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("publishProject mengubah status DRAFT milik sendiri menjadi OPEN")
    void publishProject_ownerDraft_becomesOpen() {
        UUID userId = UUID.randomUUID();
        Project project = new Project(client(user(userId), UUID.randomUUID()),
                category(UUID.randomUUID()), "Siap Tayang", null, null, null, null, null);
        project.setProjectId(UUID.randomUUID());
        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        ProjectResponse response = projectService.publishProject(project.getProjectId(), userId);

        assertEquals(ProjectStatus.OPEN, response.getStatus());
    }

    @Test
    @DisplayName("updateProject menolak perubahan oleh pengguna yang bukan pemilik")
    void updateProject_notOwner_throwsForbidden() {
        UUID ownerId = UUID.randomUUID();
        Project project = new Project(client(user(ownerId), UUID.randomUUID()),
                category(UUID.randomUUID()), "Milik Orang Lain", null, null, null, null, null);
        project.setProjectId(UUID.randomUUID());
        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setCategoryId(UUID.randomUUID());
        request.setTitle("Direbut");

        assertThrows(RoleNotAllowedException.class,
                () -> projectService.updateProject(project.getProjectId(), UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("deleteProject menolak penghapusan proyek yang sudah tidak berstatus DRAFT")
    void deleteProject_notDraft_throwsBadRequest() {
        UUID userId = UUID.randomUUID();
        Project project = new Project(client(user(userId), UUID.randomUUID()),
                category(UUID.randomUUID()), "Sudah Tayang", null, null, null, null, null);
        project.setProjectId(UUID.randomUUID());
        project.setStatus(ProjectStatus.OPEN);
        when(projectRepository.findById(project.getProjectId())).thenReturn(Optional.of(project));

        assertThrows(BadRequestException.class,
                () -> projectService.deleteProject(project.getProjectId(), userId));
        verify(projectRepository, never()).delete(any());
    }
}
