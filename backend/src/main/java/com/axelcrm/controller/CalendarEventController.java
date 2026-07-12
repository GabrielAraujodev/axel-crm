package com.axelcrm.controller;

import com.axelcrm.dto.CalendarEventRequest;
import com.axelcrm.dto.CalendarEventResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.CalendarEventService;
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
@RequestMapping("/api/v1/calendar-events")
@RequiredArgsConstructor
@Tag(name = "Calendar Events", description = "Endpoints for managing calendar appointments and meetings")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @GetMapping
    @Operation(summary = "List all calendar events")
    public ResponseEntity<Page<CalendarEventResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(calendarEventService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a calendar event by ID")
    public ResponseEntity<CalendarEventResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(calendarEventService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new calendar event")
    public ResponseEntity<CalendarEventResponse> create(@Valid @RequestBody CalendarEventRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(calendarEventService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing calendar event")
    public ResponseEntity<CalendarEventResponse> update(@PathVariable UUID id, @Valid @RequestBody CalendarEventRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(calendarEventService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a calendar event")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        calendarEventService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }
}
