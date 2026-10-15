package com.nexscale.api.skill.controller;
import java.util.List;
import java.util.UUID;
import com.nexscale.api.skill.dto.SkillResponse;
import com.nexscale.api.skill.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillService skillService;

    //1. Constructor Injection
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    //2. Endpoint mengambil semua daftar skill(GET /api/skills)
   @GetMapping
public ResponseEntity<List<SkillResponse>> getAllSkills() {
    List<SkillResponse> skills = skillService.getAllSkills();
    return ResponseEntity.ok(skills);
}

    //3. Endpoint mengambil detail 1 skill berdasarkan ID (GET /api/skills/{id})
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse>getSkillById(@PathVariable UUID id) {
        SkillResponse skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }
}