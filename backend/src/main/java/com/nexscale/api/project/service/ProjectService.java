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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final CategoryRepository categoryRepository;

    public ProjectService(ProjectRepository projectRepository, ClientProfileRepository clientProfileRepository,
            CategoryRepository categoryRepository) {
        this.projectRepository = projectRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProjectResponse createProject(UUID currentUserId, CreateProjectRequest request) {
        validateBudget(request.getBudgetMin(), request.getBudgetMax());
        ClientProfile client = clientProfileRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil klien tidak ditemukan"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));

        Project project = new Project(
                client, category, request.getTitle().trim(), request.getDescription(),
                request.getBudgetMin(), request.getBudgetMax(),
                request.getDurationEstimate(), request.getDeadline()
        );
        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getOpenProjects() {
        return projectRepository.findByStatus(ProjectStatus.OPEN).stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(UUID currentUserId) {
        ClientProfile client = clientProfileRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profil klien tidak ditemukan"));
        return projectRepository.findByClient_ClientId(client.getClientId()).stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId, UUID currentUserId) {
        Project project = findProject(projectId);
        boolean isOwner = currentUserId != null
                && project.getClient().getUser().getUserId().equals(currentUserId);
        if (project.getStatus() != ProjectStatus.OPEN && !isOwner) {
            throw new ResourceNotFoundException("Proyek tidak ditemukan");
        }
        return ProjectResponse.fromEntity(project);
    }

    @Transactional
    public ProjectResponse updateProject(UUID projectId, UUID currentUserId, UpdateProjectRequest request) {
        validateBudget(request.getBudgetMin(), request.getBudgetMax());
        Project project = findOwnedDraft(projectId, currentUserId);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));

        project.setCategory(category);
        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription());
        project.setBudgetMin(request.getBudgetMin());
        project.setBudgetMax(request.getBudgetMax());
        project.setDurationEstimate(request.getDurationEstimate());
        project.setDeadline(request.getDeadline());
        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse publishProject(UUID projectId, UUID currentUserId) {
        Project project = findOwnedDraft(projectId, currentUserId);
        project.setStatus(ProjectStatus.OPEN);
        return ProjectResponse.fromEntity(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(UUID projectId, UUID currentUserId) {
        projectRepository.delete(findOwnedDraft(projectId, currentUserId));
    }

    private Project findProject(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyek tidak ditemukan"));
    }

    private Project findOwnedDraft(UUID projectId, UUID currentUserId) {
        Project project = findProject(projectId);
        if (!project.getClient().getUser().getUserId().equals(currentUserId)) {
            throw new RoleNotAllowedException("Anda bukan pemilik proyek ini");
        }
        if (project.getStatus() != ProjectStatus.DRAFT) {
            throw new BadRequestException("Hanya proyek berstatus DRAFT yang dapat diubah");
        }
        return project;
    }

    private void validateBudget(BigDecimal min, BigDecimal max) {
        if (min != null && max != null && max.compareTo(min) < 0) {
            throw new BadRequestException("Budget maksimum tidak boleh lebih kecil dari budget minimum");
        }
    }
}
