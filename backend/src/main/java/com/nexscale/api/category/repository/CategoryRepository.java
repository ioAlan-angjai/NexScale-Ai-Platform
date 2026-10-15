package com.nexscale.api.category.repository;
import com.nexscale.api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // 1. Mencari kategori berdasarkan nama persis
    Optional<Category> findByName(String name);
    // 2. Mengecek apakah kategori dengan nama tertentu sudah terdaftar
    boolean existsByName(String name);

}