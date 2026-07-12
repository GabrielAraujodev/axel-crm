package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimelineItemResponse(
    UUID id,
    String type, // "NOTE" or "SYSTEM_LOG"
    String action, // "CREATE", "UPDATE", "DELETE", or "ADD_NOTE", etc.
    String content, // Content of the note or formatted audit description
    UUID userId,
    String userName,
    LocalDateTime createdAt
) {
}
