package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.ProspectStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;

/**
 * Represents a cold contact before they demonstrate real interest (Lead).
 */
@Entity
@Table(name = "prospects")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Prospect extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadSource source = LeadSource.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProspectStage stage = ProspectStage.PROSPECTING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "converted_lead_id")
    private Lead convertedLead;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;
}
