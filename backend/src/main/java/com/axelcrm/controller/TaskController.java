package com.axelcrm.controller;

import com.axelcrm.dto.TaskRequest;
import com.axelcrm.dto.TaskResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.TaskService;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints for managing project tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "List all tasks in the organization")
    public ResponseEntity<Page<TaskResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(taskService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(taskService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(taskService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(taskService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a task")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        taskService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
