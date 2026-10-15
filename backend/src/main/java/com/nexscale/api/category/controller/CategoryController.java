package com.nexscale.api.category.controller;

import com.nexscale.api.category.dto.CategoryRequest;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    //1. constructor injection
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //2. Endpoint mengambil semua kategori
    @GetMapping
    public ResponseEntity<List<CategoryResponse>>getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    //3. Endpoint mengambil detail 1 kategori berdasarkan ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse>getCategoryById(@PathVariable UUID id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
    //4. Endpoint membuat kategori baru (POST /api/categories) - admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
// 5. Endpoint memperbarui kategori (PUT /api/categories/{id}) — Admin
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(updated);
    }

    //6. Endpoint menghapus kategori (DELETE /api/categories/{id}) - Admin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return
        ResponseEntity.noContent().build();
    }

        
}