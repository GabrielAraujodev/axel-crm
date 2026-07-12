package com.axelcrm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StageTransitionRequest(
    @NotNull UUID dealId,
    @NotNull UUID stageId,
    String reason
) {
}
