package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeadDetailDtos {

    public record LeadNoteRequest(
            String content
    ) {}

    public record LeadNoteResponse(
            UUID id,
            String content,
            UUID createdBy,
            LocalDateTime createdAt
    ) {}

    public record LeadTimelineItemResponse(
            UUID id,
            String type, // "NOTE", "SYSTEM"
            String title,
            String content,
            UUID createdBy,
            LocalDateTime createdAt
    ) {}
}
