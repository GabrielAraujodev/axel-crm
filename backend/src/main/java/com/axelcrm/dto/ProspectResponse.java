package com.axelcrm.dto;

import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.ProspectStage;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProspectResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String company,
    LeadSource source,
    ProspectStage stage,
    String notes,
    UUID convertedLeadId,
    LocalDateTime convertedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
