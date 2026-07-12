package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PartnerResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String company,
    String bankDetails,
    BigDecimal commissionPercentage,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    // KPIs
    Long totalReferrals,
    Long proposalsSent,
    BigDecimal conversionRate
) {
}
