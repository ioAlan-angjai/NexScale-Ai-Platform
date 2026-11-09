package com.nexscale.api.project.dto;

import com.nexscale.api.project.entity.Project;
import com.nexscale.api.project.entity.ProjectStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ProjectResponse {

    private UUID projectId;
    private UUID clientId;
    private UUID categoryId;
    private String categoryName;
    private String title;
    private String description;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private Integer durationEstimate;
    private ProjectStatus status;
    private LocalDate deadline;
    private Instant createdAt;
    private Instant updatedAt;

    public ProjectResponse() {
    }

    public static ProjectResponse fromEntity(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.projectId = project.getProjectId();
        response.clientId = project.getClient().getClientId();
        response.categoryId = project.getCategory().getCategoryId();
        response.categoryName = project.getCategory().getName();
        response.title = project.getTitle();
        response.description = project.getDescription();
        response.budgetMin = project.getBudgetMin();
        response.budgetMax = project.getBudgetMax();
        response.durationEstimate = project.getDurationEstimate();
        response.status = project.getStatus();
        response.deadline = project.getDeadline();
        response.createdAt = project.getCreatedAt();
        response.updatedAt = project.getUpdatedAt();
        return response;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBudgetMin() {
        return budgetMin;
    }

    public BigDecimal getBudgetMax() {
        return budgetMax;
    }

    public Integer getDurationEstimate() {
        return durationEstimate;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}
