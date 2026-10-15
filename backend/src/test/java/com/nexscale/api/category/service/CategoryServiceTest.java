package com.nexscale.api.category.service;

import com.nexscale.api.category.dto.CategoryRequest;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.entity.Category;
import com.nexscale.api.category.repository.CategoryRepository;
import com.nexscale.api.common.exception.DuplicateResourceException;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // ===== READ (sudah ada sebelumnya) =====

    @Test
    @DisplayName("getAllCategories berhasil mengembalikan daftar seluruh kategori yang dipetakan ke CategoryResponse")
    void getAllCategories_success_returnsCategoryResponseList() {
        Category cat1 = new Category("Computer Vision", "Proyek berbasis citra dan visual AI");
        cat1.setCategoryId(UUID.randomUUID());

        Category cat2 = new Category("Natural Language Processing", "Proyek pengolahan bahasa alami dan LLM");
        cat2.setCategoryId(UUID.randomUUID());

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Computer Vision", result.get(0).getName());
        assertEquals("Natural Language Processing", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getCategoryById dengan ID terdaftar berhasil mengembalikan CategoryResponse yang sesuai")
    void getCategoryById_found_returnsCategoryResponse() {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category("Predictive Analytics", "Machine learning prediktif data tabular");
        category.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("Predictive Analytics", result.getName());
        assertEquals("Machine learning prediktif data tabular", result.getDescription());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("getCategoryById dengan ID tidak terdaftar melempar ResourceNotFoundException")
    void getCategoryById_notFound_throwsResourceNotFoundException() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(categoryId)
        );

        assertEquals("Kategori tidak ditemukan", exception.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    // ===== CREATE =====

    @Test
    @DisplayName("createCategory berhasil membuat kategori baru dan mengembalikan CategoryResponse")
    void createCategory_success_returnsCategoryResponse() {
        CategoryRequest request = new CategoryRequest("Generative AI", "Proyek generatif berbasis AI");

        Category savedCategory = new Category("Generative AI", "Proyek generatif berbasis AI");
        savedCategory.setCategoryId(UUID.randomUUID());

        when(categoryRepository.existsByName("Generative AI")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("Generative AI", result.getName());
        assertEquals("Proyek generatif berbasis AI", result.getDescription());
        verify(categoryRepository, times(1)).existsByName("Generative AI");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("createCategory dengan nama yang sudah terdaftar melempar DuplicateResourceException")
    void createCategory_duplicateName_throwsDuplicateResourceException() {
        CategoryRequest request = new CategoryRequest("Computer Vision", "Duplikat");

        when(categoryRepository.existsByName("Computer Vision")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.createCategory(request)
        );

        assertTrue(exception.getMessage().contains("Computer Vision"));
        verify(categoryRepository, times(1)).existsByName("Computer Vision");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    // ===== UPDATE =====

    @Test
    @DisplayName("updateCategory berhasil memperbarui nama dan deskripsi kategori")
    void updateCategory_success_returnsUpdatedCategoryResponse() {
        UUID categoryId = UUID.randomUUID();
        Category existingCategory = new Category("Old Name", "Old Description");
        existingCategory.setCategoryId(categoryId);

        CategoryRequest request = new CategoryRequest("New Name", "New Description");

        Category updatedCategory = new Category("New Name", "New Description");
        updatedCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("New Name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponse result = categoryService.updateCategory(categoryId, request);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory dengan ID tidak ditemukan melempar ResourceNotFoundException")
    void updateCategory_notFound_throwsResourceNotFoundException() {
        UUID categoryId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("Any Name", "Any Desc");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, request)
        );

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory dengan nama baru yang sudah dipakai kategori lain melempar DuplicateResourceException")
    void updateCategory_duplicateName_throwsDuplicateResourceException() {
        UUID categoryId = UUID.randomUUID();
        Category existingCategory = new Category("Original Name", "Desc");
        existingCategory.setCategoryId(categoryId);

        CategoryRequest request = new CategoryRequest("Existing Other Name", "New Desc");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("Existing Other Name")).thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.updateCategory(categoryId, request)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("updateCategory dengan nama yang sama (case-insensitive) tidak melempar duplikasi")
    void updateCategory_sameNameDifferentCase_success() {
        UUID categoryId = UUID.randomUUID();
        Category existingCategory = new Category("Computer Vision", "Old Desc");
        existingCategory.setCategoryId(categoryId);

        CategoryRequest request = new CategoryRequest("computer vision", "Updated Desc");

        Category updatedCategory = new Category("computer vision", "Updated Desc");
        updatedCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponse result = categoryService.updateCategory(categoryId, request);

        assertNotNull(result);
        assertEquals("computer vision", result.getName());
        verify(categoryRepository, never()).existsByName(anyString());
    }

    // ===== DELETE =====

    @Test
    @DisplayName("deleteCategory berhasil menghapus kategori yang ada")
    void deleteCategory_success_deletesCategory() {
        UUID categoryId = UUID.randomUUID();
        Category existingCategory = new Category("To Delete", "Will be deleted");
        existingCategory.setCategoryId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(existingCategory);
    }

    @Test
    @DisplayName("deleteCategory dengan ID tidak ditemukan melempar ResourceNotFoundException")
    void deleteCategory_notFound_throwsResourceNotFoundException() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId)
        );

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
