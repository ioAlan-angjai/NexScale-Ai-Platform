package com.nexscale.api.project.repository;

import com.nexscale.api.project.entity.Project;
import com.nexscale.api.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByClient_ClientId(UUID clientId);
}
