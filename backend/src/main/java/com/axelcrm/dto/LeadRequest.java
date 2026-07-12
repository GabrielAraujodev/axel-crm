package com.axelcrm.dto;

import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.LeadStage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating or updating a Lead.
 * English: Lead request payload.
 */
public record LeadRequest(
    @NotBlank @Size(max = 200) String name,
    @Email @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 255) String companyName,
    @Size(max = 255) String jobTitle,
    @NotNull LeadSource source,
    @NotNull LeadStage stage,
    String notes,
    Integer score,
    BigDecimal estimatedValue,
    UUID assignedToUserId,
    UUID partnerId
) {
}
