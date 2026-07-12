package com.axelcrm.controller;

import com.axelcrm.dto.PipelineRequest;
import com.axelcrm.dto.PipelineResponse;
import com.axelcrm.dto.PipelineStageRequest;
import com.axelcrm.dto.PipelineStageResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.PipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pipelines")
@RequiredArgsConstructor
@Tag(name = "Pipelines", description = "Endpoints for managing pipelines and stage steps")
public class PipelineController {

    private final PipelineService pipelineService;

    @GetMapping
    @Operation(summary = "List all pipelines")
    public ResponseEntity<Page<PipelineResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a pipeline by ID")
    public ResponseEntity<PipelineResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new pipeline")
    public ResponseEntity<PipelineResponse> create(@Valid @RequestBody PipelineRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing pipeline")
    public ResponseEntity<PipelineResponse> update(@PathVariable UUID id, @Valid @RequestBody PipelineRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a pipeline")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        pipelineService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    // Pipeline Stages

    @GetMapping("/{pipelineId}/stages")
    @Operation(summary = "Get all stages for a pipeline")
    public ResponseEntity<List<PipelineStageResponse>> findStagesByPipelineId(@PathVariable UUID pipelineId) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.findStagesByPipelineId(organizationId, pipelineId));
    }

    @PostMapping("/stages")
    @Operation(summary = "Create a new pipeline stage")
    public ResponseEntity<PipelineStageResponse> createStage(@Valid @RequestBody PipelineStageRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.createStage(organizationId, request));
    }

    @PutMapping("/stages/{stageId}")
    @Operation(summary = "Update an existing pipeline stage")
    public ResponseEntity<PipelineStageResponse> updateStage(@PathVariable UUID stageId, @Valid @RequestBody PipelineStageRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(pipelineService.updateStage(organizationId, stageId, request));
    }

    @DeleteMapping("/stages/{stageId}")
    @Operation(summary = "Soft delete a pipeline stage")
    public ResponseEntity<Void> deleteStage(@PathVariable UUID stageId) {
        UUID organizationId = TenantContext.getOrganizationId();
        pipelineService.deleteStage(organizationId, stageId);
        return ResponseEntity.noContent().build();
    }
}
