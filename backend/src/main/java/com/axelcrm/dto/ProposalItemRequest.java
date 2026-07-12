package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating or updating a Proposal item.
 */
public record ProposalItemRequest(
    UUID id,
    @NotBlank @Size(max = 500) String description,
    @NotNull @Positive Integer quantity,
    @NotNull BigDecimal unitPrice,
    BigDecimal discountAmount
) {
}
