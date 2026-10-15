package com.nexscale.api.category.dto;
import com.nexscale.api.category.entity.Category;
import java.time.Instant;
import java.util.UUID;

public class CategoryResponse {
    private UUID categoryId;
    private String name;
    private String description;
    private Instant createdAt;

    // 1. Constructor tanpa argumen
    public CategoryResponse() {}

    // 2. Constructor berparameter
    public CategoryResponse(UUID categoryId, String name, String description, Instant createdAt) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // 3. Static helper untuk konversi dari entity category ke DTO
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
            category.getCategoryId(),
            category.getName(),
            category.getDescription(),
            category.getCreatedAt()
        );
    }
    // 4. Getters dan setters
     public UUID getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}