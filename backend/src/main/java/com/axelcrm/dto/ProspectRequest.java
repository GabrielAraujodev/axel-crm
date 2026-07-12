package com.axelcrm.dto;

import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.ProspectStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProspectRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) String email,
    @Size(max = 50) String phone,
    @Size(max = 255) String company,
    LeadSource source,
    ProspectStage stage,
    String notes
) {
}
