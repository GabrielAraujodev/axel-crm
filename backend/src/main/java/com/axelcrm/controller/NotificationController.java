package com.axelcrm.controller;

import com.axelcrm.dto.NotificationRequest;
import com.axelcrm.dto.NotificationResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.NotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user and system notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List all notifications for the organization")
    public ResponseEntity<Page<NotificationResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(notificationService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a notification by ID")
    public ResponseEntity<NotificationResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(notificationService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new notification")
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(notificationService.create(organizationId, request));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(notificationService.markAsRead(organizationId, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a notification")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        notificationService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
