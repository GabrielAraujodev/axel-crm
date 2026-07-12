package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * DTO for creating or updating a BankAccount.
 */
@Schema(description = "Bank account request payload")
public record BankAccountRequest(
    @NotBlank
    @Schema(description = "Account display name", example = "Conta Principal")
    String name,

    @Schema(description = "Bank name", example = "Banco do Brasil")
    String bankName,

    @NotBlank
    @Schema(description = "Account number", example = "12345-6")
    String accountNumber,

    @Schema(description = "Bank agency", example = "0001")
    String agency,

    @Schema(description = "Current account balance")
    BigDecimal currentBalance,

    @Schema(description = "Whether the account is active", example = "true")
    boolean active
) {
}
