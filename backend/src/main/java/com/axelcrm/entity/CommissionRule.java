package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;

/**
 * Rule used to calculate commissions.
 */
@Entity
@Table(name = "commission_rules")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "percentage", precision = 5, scale = 4, nullable = false)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(name = "min_value", precision = 15, scale = 2)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 15, scale = 2)
    private BigDecimal maxValue;

    @Column(nullable = false)
    private boolean active = true;
}
