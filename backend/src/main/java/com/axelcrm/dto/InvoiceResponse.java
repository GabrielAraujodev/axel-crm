package com.axelcrm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record InvoiceResponse(
    UUID id,
    String invoiceNumber,
    UUID clientId,
    String clientName,
    UUID contractId,
    String contractTitle,
    LocalDate issueDate,
    LocalDate dueDate,
    LocalDate paidDate,
    String status,
    BigDecimal subtotal,
    BigDecimal taxAmount,
    BigDecimal discountAmount,
    BigDecimal total,
    String notes,
    String paymentMethod,
    BigDecimal paidAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
