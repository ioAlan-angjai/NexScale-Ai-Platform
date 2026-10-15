package com.nexscale.api.skill.entity;
import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "skill_id", updatable = false, nullable = false)
    private UUID skillId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    //1. Constructor tanpa argumen (wajib untuk JPA)
    public Skill() {

    }

    //2. Constructor berparameter
    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }


    //3. Getter dan Setters
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

    //4. Equals dan HashCode berbasis Primary Key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill =(Skill) o;
        return Objects.equals(skillId, skill.skillId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(skillId);
    }
}