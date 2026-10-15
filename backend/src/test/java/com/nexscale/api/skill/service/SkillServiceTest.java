package com.nexscale.api.skill.service;

import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.skill.dto.SkillResponse;
import com.nexscale.api.skill.entity.Skill;
import com.nexscale.api.skill.repository.SkillRepository;
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
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    @DisplayName("getAllSkills berhasil mengembalikan daftar seluruh skill yang dipetakan ke SkillResponse")
    void getAllSkills_success_returnsSkillResponseList() {
        Skill skill1 = new Skill("PyTorch", "Framework deep learning berbasis Python");
        skill1.setSkillId(UUID.randomUUID());

        Skill skill2 = new Skill("TensorFlow", "Platform open-source untuk machine learning");
        skill2.setSkillId(UUID.randomUUID());

        when(skillRepository.findAll()).thenReturn(List.of(skill1, skill2));

        List<SkillResponse> result = skillService.getAllSkills();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PyTorch", result.get(0).getName());
        assertEquals("TensorFlow", result.get(1).getName());
        verify(skillRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getSkillById dengan ID terdaftar berhasil mengembalikan SkillResponse yang sesuai")
    void getSkillById_found_returnsSkillResponse() {
        UUID skillId = UUID.randomUUID();
        Skill skill = new Skill("LangChain", "Framework orkestrasi aplikasi LLM");
        skill.setSkillId(skillId);

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        SkillResponse result = skillService.getSkillById(skillId);

        assertNotNull(result);
        assertEquals(skillId, result.getSkillId());
        assertEquals("LangChain", result.getName());
        assertEquals("Framework orkestrasi aplikasi LLM", result.getDescription());
        verify(skillRepository, times(1)).findById(skillId);
    }

    @Test
    @DisplayName("getSkillById dengan ID tidak terdaftar melempar ResourceNotFoundException")
    void getSkillById_notFound_throwsResourceNotFoundException() {
        UUID skillId = UUID.randomUUID();

        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> skillService.getSkillById(skillId)
        );

        assertEquals("Skill tidak ditemukan", exception.getMessage());
        verify(skillRepository, times(1)).findById(skillId);
    }
}
