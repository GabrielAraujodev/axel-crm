package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for BankAccount responses.
 */
public record BankAccountResponse(
    UUID id,
    String name,
    String bankName,
    String accountNumber,
    String agency,
    BigDecimal currentBalance,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
