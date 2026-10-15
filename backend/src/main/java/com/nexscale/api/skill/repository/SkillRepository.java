package com.nexscale.api.skill.repository;

import com.nexscale.api.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    // 1. Mencari skill berdasarkan nama persis
    Optional<Skill> findByName(String name);

    // 2. Mengecek apakah skill dengan nama tertentu sudah terdaftar (berguna untuk validasi create nanti)
    boolean existsByName(String name);
}
