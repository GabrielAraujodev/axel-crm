package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LegalProcessRequest(
    @NotBlank String cnjNumber,
    String court,
    LocalDate distributionDate,
    BigDecimal value,
    String status,
    String description
) {
}
