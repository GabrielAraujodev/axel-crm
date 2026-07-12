package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.LeadStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;
import com.axelcrm.auth.entity.User;

/**
 * Represents a sales lead before conversion to a client.
 */
@Entity
@Table(name = "leads")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Lead extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadSource source = LeadSource.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStage stage = LeadStage.NEW;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Integer score = 0;

    @Column(precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "last_contact_at")
    private LocalDateTime lastContactAt;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_client_id")
    private Client convertedClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;
}
