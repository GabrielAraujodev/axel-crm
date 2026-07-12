package com.axelcrm.repository;

import com.axelcrm.entity.Project;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Project} entities.
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    java.util.List<Project> findByOrganization_IdAndDeletedAtIsNull(UUID orgId);

    Page<Project> findByOrganization_IdAndDeletedAtIsNull(UUID orgId, Pageable pageable);

    Optional<Project> findByIdAndOrganization_Id(UUID id, UUID orgId);

    Page<Project> findByClient_IdAndClient_Organization_Id(UUID clientId, UUID orgId, Pageable pageable);

    long countByOrganization_IdAndDeletedAtIsNull(UUID orgId);
}
