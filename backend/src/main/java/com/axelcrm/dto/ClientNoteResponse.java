package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientNoteResponse(
    UUID id,
    UUID clientId,
    UUID userId,
    String userName,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
