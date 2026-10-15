package com.nexscale.api.skill.dto;
import com.nexscale.api.skill.entity.Skill;
import java.util.UUID;

public class SkillResponse {
    private UUID skillId;
    private String name;
    private String description;

    //1. Constructor tanpa argumen
    public SkillResponse() {}

    //2. Constructor berparameter
    public SkillResponse(UUID skillId, String name, String description) {
        this.skillId = skillId;
        this.name = name;
        this.description = description;
    }

    //3. Static factory method untuk memetakan entity Skill ke DTO
    public static SkillResponse fromEntity(Skill skill) {
        return new SkillResponse(
            skill.getSkillId(),
            skill.getName(),
            skill.getDescription()
        );
    }

    //4. Getter dan setters
     public UUID getSkillId() {
        return skillId;
    }
    public void setSkillId(UUID skillId) {
        this.skillId = skillId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}