package com.axelcrm.dto;

import com.axelcrm.entity.enums.CampaignType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Campaign responses.
 */
public record CampaignResponse(
    UUID id,
    String name,
    CampaignType type,
    String content,
    LocalDateTime scheduledAt,
    LocalDateTime sentAt,
    Integer recipientsCount,
    Integer sentCount,
    Integer openCount,
    Integer clickCount,
    String status,
    UUID createdByUserId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
