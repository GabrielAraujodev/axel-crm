package com.axelcrm.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for Proposal item responses.
 */
public record ProposalItemResponse(
    UUID id,
    UUID proposalId,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal discountAmount,
    BigDecimal total
) {
}
