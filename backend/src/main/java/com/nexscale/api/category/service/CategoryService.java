package com.nexscale.api.category.service;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.entity.Category;
import com.nexscale.api.category.repository.CategoryRepository;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexscale.api.category.dto.CategoryRequest;
import com.nexscale.api.common.exception.DuplicateResourceException;
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

    //4. Membuat kategori baru(Admin)
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String trimmedName = request.getName().trim();
        if (categoryRepository.existsByName(trimmedName)){
            throw new DuplicateResourceException("Kategori dengan nama '" + trimmedName + "' sudah terdaftar");
        }

        Category category = new Category(trimmedName, request.getDescription());
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.fromEntity(savedCategory);
    }

    //5. Memperbarui kategori yang sudah ada(Admin)
    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(()-> new ResourceNotFoundException("Kategori tidak ditemukan"));

        String newName = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(newName) && categoryRepository.existsByName(newName)) {
            throw new DuplicateResourceException("Kategori dengan nama '" +newName + "' sudah terdaftar");
        }
        category.setName(newName);

        category.setDescription(request.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        return
        CategoryResponse.fromEntity(updatedCategory);
    }

    //6. Menghapus kategori(admin)
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
        .orElseThrow(()-> new ResourceNotFoundException("Kategori tidak ditemukan"));

        categoryRepository.delete(category);
    }
}