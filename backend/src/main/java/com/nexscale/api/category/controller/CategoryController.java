package com.nexscale.api.category.controller;
import com.nexscale.api.category.dto.CategoryResponse;
import com.nexscale.api.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/categories")
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
}