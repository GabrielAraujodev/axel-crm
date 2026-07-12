package com.axelcrm.service;

import com.axelcrm.dto.PipelineRequest;
import com.axelcrm.dto.PipelineResponse;
import com.axelcrm.dto.PipelineStageRequest;
import com.axelcrm.dto.PipelineStageResponse;
import com.axelcrm.entity.Pipeline;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.PipelineRepository;
import com.axelcrm.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineStageRepository pipelineStageRepository;

    public Page<PipelineResponse> findAll(UUID organizationId, Pageable pageable) {
        return pipelineRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public PipelineResponse findById(UUID organizationId, UUID id) {
        return pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));
    }

    @Transactional
    public PipelineResponse create(UUID organizationId, PipelineRequest request) {
        Pipeline pipeline = new Pipeline();
        pipeline.setName(request.name());
        pipeline.setDescription(request.description());
        pipeline.setActive(request.active());

        pipeline = pipelineRepository.save(pipeline);
        return toResponse(pipeline);
    }

    @Transactional
    public PipelineResponse update(UUID organizationId, UUID id, PipelineRequest request) {
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));
        pipeline.setName(request.name());
        pipeline.setDescription(request.description());
        pipeline.setActive(request.active());

        pipeline = pipelineRepository.save(pipeline);
        return toResponse(pipeline);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", id));
        pipeline.setDeletedAt(java.time.LocalDateTime.now());
        pipelineRepository.save(pipeline);
    }

    // Pipeline Stage Methods
    public List<PipelineStageResponse> findStagesByPipelineId(UUID organizationId, UUID pipelineId) {
        return pipelineStageRepository.findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(pipelineId, organizationId)
                .stream()
                .map(this::toStageResponse)
                .toList();
    }

    @Transactional
    public PipelineStageResponse createStage(UUID organizationId, PipelineStageRequest request) {
        Pipeline pipeline = pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.pipelineId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", request.pipelineId()));

        PipelineStage stage = new PipelineStage();
        stage.setPipeline(pipeline);
        stage.setName(request.name());
        stage.setPosition(request.position());
        stage.setWinProbability(request.winProbability() != null ? request.winProbability() : 0);
        stage.setDescription(request.description());

        stage = pipelineStageRepository.save(stage);
        return toStageResponse(stage);
    }

    @Transactional
    public PipelineStageResponse updateStage(UUID organizationId, UUID stageId, PipelineStageRequest request) {
        PipelineStage stage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(stageId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", stageId));

        stage.setName(request.name());
        stage.setPosition(request.position());
        stage.setWinProbability(request.winProbability() != null ? request.winProbability() : 0);
        stage.setDescription(request.description());

        stage = pipelineStageRepository.save(stage);
        return toStageResponse(stage);
    }

    @Transactional
    public void deleteStage(UUID organizationId, UUID stageId) {
        PipelineStage stage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(stageId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", stageId));
        stage.setDeletedAt(java.time.LocalDateTime.now());
        pipelineStageRepository.save(stage);
    }

    public PipelineResponse toResponse(Pipeline pipeline) {
        List<PipelineStageResponse> stages = pipelineStageRepository.findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(pipeline.getId(), pipeline.getOrganization() != null ? pipeline.getOrganization().getId() : null)
                .stream()
                .map(this::toStageResponse)
                .toList();

        return new PipelineResponse(
                pipeline.getId(),
                pipeline.getName(),
                pipeline.getDescription(),
                pipeline.isActive(),
                stages,
                pipeline.getCreatedAt(),
                pipeline.getUpdatedAt()
        );
    }

    public PipelineStageResponse toStageResponse(PipelineStage stage) {
        return new PipelineStageResponse(
                stage.getId(),
                stage.getPipeline() != null ? stage.getPipeline().getId() : null,
                stage.getName(),
                stage.getPosition(),
                stage.getWinProbability(),
                stage.getDescription(),
                stage.getCreatedAt(),
                stage.getUpdatedAt()
        );
    }
}
