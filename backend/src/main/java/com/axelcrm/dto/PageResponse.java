package com.axelcrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Generic paginated response wrapper.
 */
@Schema(description = "Paginated response wrapper")
public record PageResponse<T>(
        @Schema(description = "List of items in the current page")
        List<T> content,

        @Schema(description = "Current page number", example = "0")
        int pageNumber,

        @Schema(description = "Page size", example = "20")
        int pageSize,

        @Schema(description = "Total number of elements")
        long totalElements,

        @Schema(description = "Total number of pages")
        int totalPages
) {
}
