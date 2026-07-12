package com.axelcrm.dto;

import com.axelcrm.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for FinancialTransaction responses.
 */
public record FinancialTransactionResponse(
    UUID id,
    String description,
    TransactionType transactionType,
    BigDecimal amount,
    LocalDate transactionDate,
    LocalDate dueDate,
    LocalDateTime paidAt,
    Boolean paid,
    String category,
    UUID bankAccountId,
    String bankAccountName,
    UUID clientId,
    String clientName,
    UUID dealId,
    String dealTitle,
    UUID chartAccountId,
    String chartAccountName,
    String chartAccountCode,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public FinancialTransactionResponse(
        UUID id, String description, TransactionType transactionType, BigDecimal amount,
        LocalDate transactionDate, LocalDate dueDate, LocalDateTime paidAt, Boolean paid,
        String category, UUID bankAccountId, String bankAccountName, UUID clientId,
        String clientName, UUID dealId, String dealTitle, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this(id, description, transactionType, amount, transactionDate, dueDate, paidAt, paid, category, bankAccountId, bankAccountName, clientId, clientName, dealId, dealTitle, null, null, null, createdAt, updatedAt);
    }
}
