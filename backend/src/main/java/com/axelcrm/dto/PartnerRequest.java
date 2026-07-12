package com.axelcrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PartnerRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 255) String company,
    String bankDetails,
    BigDecimal commissionPercentage
) {
}
