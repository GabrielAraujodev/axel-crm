package com.axelcrm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientAttachmentResponse(
    UUID id,
    UUID clientId,
    UUID userId,
    String userName,
    String fileName,
    String fileType,
    Long fileSize,
    LocalDateTime createdAt
) {
}
