package com.axelcrm.controller;

import com.axelcrm.dto.TimeEntryRequest;
import com.axelcrm.dto.TimeEntryResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.TimeEntryService;
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
@RequestMapping("/api/v1/time-entries")
@RequiredArgsConstructor
@Tag(name = "Time Entries", description = "Endpoints for managing task work logs and timesheets")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @GetMapping
    @Operation(summary = "List all time entries")
    public ResponseEntity<Page<TimeEntryResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(timeEntryService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a time entry by ID")
    public ResponseEntity<TimeEntryResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(timeEntryService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new time entry")
    public ResponseEntity<TimeEntryResponse> create(@Valid @RequestBody TimeEntryRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(timeEntryService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing time entry")
    public ResponseEntity<TimeEntryResponse> update(@PathVariable UUID id, @Valid @RequestBody TimeEntryRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(timeEntryService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a time entry")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        timeEntryService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
