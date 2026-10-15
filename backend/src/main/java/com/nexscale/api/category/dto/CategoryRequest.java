package com.nexscale.api.category.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
    @NotBlank(message = "Nama kategori wajib diisi")
    @Size(max = 100, message = "Nama kategori maksimal 100 kaakter")
    private String name;

    private String description;

    //1. Constructor tanpa argumen
    public CategoryRequest(){}

    //2. Constructor berparameter
    public CategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    //3. Getter dan setter
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
}