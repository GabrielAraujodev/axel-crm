package com.axelcrm.dto;

import com.axelcrm.entity.enums.ChartOfAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChartOfAccountRequest(
    @NotBlank String code,
    @NotBlank String name,
    @NotNull ChartOfAccountType type,
    UUID parentId
) {
}
