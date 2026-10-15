package com.nexscale.api.skill.service;
import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.skill.dto.SkillResponse;
import com.nexscale.api.skill.entity.Skill;
import com.nexscale.api.skill.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    //1. Constructor injection(menghubungkan service ke repository)
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    //2. Mengambil semua daftar skill(Read - only)
    @Transactional(readOnly = true)
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
        .map(SkillResponse::fromEntity) .toList();
    }

    //3.Mengambil satu skill berdasarkan ID (Read-Only)
    @Transactional(readOnly = true)
    public SkillResponse getSkillById(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
        .orElseThrow(()->new ResourceNotFoundException("Skill tidak ditemukan"));
        return SkillResponse.fromEntity(skill);
    }
}