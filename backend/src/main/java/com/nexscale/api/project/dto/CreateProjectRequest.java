package com.nexscale.api.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateProjectRequest {
    @NotNull(message = "Kategori wajib dipilih")
    private UUID categoryId;

    @NotBlank(message = "Judul Proyek wajib diisi")
    @Size(max = 200, message = "Judul Proyek maksimal 200 karakter")
    private String title;

    private String description;

    @DecimalMin(value = "0.0", message = "Budget minimum tidak boleh negative")
    private BigDecimal budgetMin;

    @DecimalMin(value = "0.0", message = "Budget Maksimum tidak boleh negative")
    private BigDecimal budgetMax;

    @Positive(message = "Estimasi durasi harus lebih dari 0")
    private Integer durationEstimate;
    private LocalDate deadline;

    public CreateProjectRequest() {
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(BigDecimal budgetMin) {
        this.budgetMin = budgetMin;
    }

    public BigDecimal getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(BigDecimal budgetMax) {
        this.budgetMax = budgetMax;
    }

    public Integer getDurationEstimate() {
        return durationEstimate;
    }

    public void setDurationEstimate(Integer durationEstimate) {
        this.durationEstimate = durationEstimate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}