package com.axelcrm.repository;

import com.axelcrm.entity.Pipeline;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Pipeline entities.
 */
public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    Optional<Pipeline> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    Page<Pipeline> findByOrganization_IdAndDeletedAtIsNull(UUID organizationId, Pageable pageable);
}
