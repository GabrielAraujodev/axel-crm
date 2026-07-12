package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;

/**
 * Represents an LGPD consent record for a lead or client.
 */
@Entity
@Table(name = "lgpd_consents")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Consent extends BaseEntity {

    @Column(name = "person_email", nullable = false)
    private String personEmail;

    @Column(name = "consent_type", nullable = false)
    private String consentType;

    @Column(nullable = false)
    private boolean granted = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "consented_at", nullable = false)
    private LocalDateTime consentedAt = LocalDateTime.now();
}
