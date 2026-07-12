package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Invoice request payload")
public record InvoiceRequest(

    @Schema(description = "Human-readable invoice number", example = "INV-2025-001")
    String invoiceNumber,

    @NotNull
    @Schema(description = "Client ID")
    UUID clientId,

    @Schema(description = "Related contract ID")
    UUID contractId,

    @NotNull
    @Schema(description = "Issue date", example = "2025-01-15")
    LocalDate issueDate,

    @NotNull
    @Schema(description = "Due date", example = "2025-02-15")
    LocalDate dueDate,

    @Schema(description = "Date when payment was received")
    LocalDate paidDate,

    @Schema(description = "Status: DRAFT, ISSUED, PAID, OVERDUE, CANCELLED", example = "DRAFT")
    String status,

    @Schema(description = "Subtotal before taxes and discounts")
    BigDecimal subtotal,

    @Schema(description = "Tax amount")
    BigDecimal taxAmount,

    @Schema(description = "Discount amount")
    BigDecimal discountAmount,

    @Schema(description = "Total invoice amount")
    BigDecimal total,

    @Schema(description = "Internal notes")
    String notes,

    @Schema(description = "Payment method", example = "bank_transfer")
    String paymentMethod,

    @Schema(description = "Amount actually paid (if different from total)")
    BigDecimal paidAmount
) {
}
