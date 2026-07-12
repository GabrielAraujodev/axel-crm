package com.axelcrm.dto;

import com.axelcrm.entity.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for creating or updating a FinancialTransaction.
 */
@Schema(description = "Financial transaction request payload")
public record FinancialTransactionRequest(
    @NotBlank
    @Schema(description = "Transaction description")
    String description,

    @NotNull
    @Schema(description = "Transaction type")
    TransactionType transactionType,

    @Positive
    @NotNull
    @Schema(description = "Transaction amount")
    BigDecimal amount,

    @NotNull
    @Schema(description = "Transaction date")
    LocalDate transactionDate,

    @Schema(description = "Due date for payment")
    LocalDate dueDate,

    @Schema(description = "Payment timestamp")
    LocalDateTime paidAt,

    @Schema(description = "Whether the transaction is paid", example = "false")
    boolean paid,

    @Schema(description = "Transaction category", example = "Software")
    String category,

    @Schema(description = "Associated bank account ID")
    UUID bankAccountId,

    @Schema(description = "Associated client ID")
    UUID clientId,

    @Schema(description = "Associated deal ID")
    UUID dealId,

    @Schema(description = "Associated chart of account ID")
    UUID chartAccountId
) {
    public FinancialTransactionRequest(
        String description, TransactionType transactionType, BigDecimal amount,
        LocalDate transactionDate, LocalDate dueDate, LocalDateTime paidAt,
        boolean paid, String category, UUID bankAccountId, UUID clientId, UUID dealId
    ) {
        this(description, transactionType, amount, transactionDate, dueDate, paidAt, paid, category, bankAccountId, clientId, dealId, null);
    }
}
