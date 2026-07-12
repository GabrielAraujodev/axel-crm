package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for CalendarEvent responses.
 */
public record CalendarEventResponse(
    UUID id,
    String title,
    String description,
    LocalDateTime startTime,
    LocalDateTime endTime,
    boolean allDay,
    String location,
    UUID userId,
    UUID leadId,
    UUID clientId,
    UUID dealId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
