package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "Product request payload")
public record ProductRequest(
    @NotBlank @Size(max = 300)
    @Schema(description = "Product name", example = "Consultoria em Nuvem")
    String name,

    @Size(max = 4000)
    @Schema(description = "Product description")
    String description,

    @Size(max = 50)
    @Schema(description = "SKU / code", example = "SRV-CLOUD-001")
    String sku,

    @Size(max = 30)
    @Schema(description = "Category: SERVICE, PRODUCT, SOFTWARE, CONSULTING", example = "SERVICE")
    String category,

    @Schema(description = "Unit selling price")
    BigDecimal unitPrice,

    @Schema(description = "Cost price (for margin calculation)")
    BigDecimal costPrice,

    @Size(max = 20)
    @Schema(description = "Unit of measure: hour, month, unit, project", example = "hour")
    String unit,

    @Schema(description = "Whether the product is active for new use")
    Boolean isActive,

    @Size(max = 4000)
    @Schema(description = "Internal notes")
    String notes
) {
}
