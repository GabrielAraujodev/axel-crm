package com.axelcrm.repository;

import com.axelcrm.entity.PipelineStage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for PipelineStage entities.
 */
public interface PipelineStageRepository extends JpaRepository<PipelineStage, UUID> {

    Optional<PipelineStage> findByIdAndOrganization_IdAndDeletedAtIsNull(UUID id, UUID organizationId);

    List<PipelineStage> findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(UUID pipelineId, UUID organizationId);
}
