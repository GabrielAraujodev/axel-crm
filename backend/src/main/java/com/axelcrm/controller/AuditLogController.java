package com.axelcrm.controller;

import com.axelcrm.dto.AuditLogRequest;
import com.axelcrm.dto.AuditLogResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Endpoints for viewing system change history and audit logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "List all audit logs in the organization")
    public ResponseEntity<List<AuditLogResponse>> findAll(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        UUID organizationId = TenantContext.getOrganizationId();

        if (entityType != null) {
            return ResponseEntity.ok(auditLogService.findByEntityType(organizationId, entityType));
        }
        if (action != null) {
            return ResponseEntity.ok(auditLogService.findByAction(organizationId, action));
        }
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(auditLogService.findByDateRange(organizationId, startDate, endDate));
        }

        return ResponseEntity.ok(auditLogService.findAll(organizationId));
    }

    @PostMapping
    @Operation(summary = "Create a manual audit log entry")
    public ResponseEntity<AuditLogResponse> create(@Valid @RequestBody AuditLogRequest request) {
        return ResponseEntity.ok(auditLogService.create(request));
    }
}
