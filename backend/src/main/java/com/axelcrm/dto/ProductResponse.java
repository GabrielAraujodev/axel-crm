package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    String description,
    String sku,
    String category,
    BigDecimal unitPrice,
    BigDecimal costPrice,
    String unit,
    boolean isActive,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
