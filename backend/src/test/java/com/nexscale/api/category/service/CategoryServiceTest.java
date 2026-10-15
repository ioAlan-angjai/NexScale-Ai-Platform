package com.nexscale.api.category.service;

import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.entity.Category;
import com.nexscale.api.category.repository.CategoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

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
}
