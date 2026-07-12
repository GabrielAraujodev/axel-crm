package com.axelcrm.dto;

import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.LeadStage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.axelcrm.auth.dto.UserResponse;

/**
 * DTO for Lead responses.
 * English: Lead response payload.
 */
public record LeadResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String companyName,
    String jobTitle,
    LeadSource source,
    LeadStage stage,
    BigDecimal estimatedValue,
    String notes,
    Integer score,
    LocalDateTime convertedAt,
    UserResponse assignedTo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
