package com.axelcrm.dto;

import com.axelcrm.entity.enums.CampaignType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating or updating a Campaign.
 */
public record CampaignRequest(
    @NotBlank @Size(max = 200) String name,
    @NotNull CampaignType type,
    @Size(max = 10000) String content,
    LocalDateTime scheduledAt,
    @Size(max = 50) String status
) {
}
