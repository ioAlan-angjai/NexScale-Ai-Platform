package com.nexscale.api.category.service;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.entity.Category;
import com.nexscale.api.category.repository.CategoryRepository;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    //1. Constructor Injection(menghubungkan service ke Repository)
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository ;
    }

    //2. Mengambil semua daftar kategori(Read- only)
    @Transactional(readOnly = true)
    public List<CategoryResponse>getAllCategories() {
        return categoryRepository.findAll().stream()
        .map(CategoryResponse::fromEntity)
        .toList();
    }

    //3. Mengambil satu kategori berdasarkan ID (Read-Only)
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        return CategoryResponse.fromEntity(category);
    }
}