package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;

@Entity
@Table(name = "legal_processes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class LegalProcess extends BaseEntity {

    @Column(name = "cnj_number", nullable = false, length = 50)
    private String cnjNumber;

    @Column(length = 200)
    private String court;

    @Column(name = "distribution_date")
    private LocalDate distributionDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal value = BigDecimal.ZERO;

    @Column(length = 100)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;
}
