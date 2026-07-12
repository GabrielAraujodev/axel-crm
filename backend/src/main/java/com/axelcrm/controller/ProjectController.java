package com.axelcrm.controller;

import com.axelcrm.dto.ProjectRequest;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for managing client projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "List all projects, optionally filtered by client")
    public ResponseEntity<Page<ProjectResponse>> findAll(
            @RequestParam(required = false) UUID clientId, Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        if (clientId != null) {
            return ResponseEntity.ok(projectService.findByClient(organizationId, clientId, pageable));
        }
        return ResponseEntity.ok(projectService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a project by ID")
    public ResponseEntity<ProjectResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(projectService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(projectService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(projectService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a project")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        projectService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
