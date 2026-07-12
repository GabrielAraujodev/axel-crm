package com.axelcrm.dto;

import com.axelcrm.entity.enums.ChartOfAccountType;
import java.util.UUID;
import java.util.List;

public record ChartOfAccountResponse(
    UUID id,
    String code,
    String name,
    ChartOfAccountType type,
    UUID parentId,
    String parentName,
    Integer level,
    List<ChartOfAccountResponse> children
) {
    public ChartOfAccountResponse(
        UUID id, String code, String name, ChartOfAccountType type,
        UUID parentId, String parentName, Integer level
    ) {
        this(id, code, name, type, parentId, parentName, level, java.util.Collections.emptyList());
    }
}
