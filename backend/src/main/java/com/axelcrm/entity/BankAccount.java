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
import com.axelcrm.commons.entity.Organization;

/**
 * Organization bank account used for financial tracking.
 */
@Entity
@Table(name = "bank_accounts")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "bank")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "agency")
    private String agency;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;
}
