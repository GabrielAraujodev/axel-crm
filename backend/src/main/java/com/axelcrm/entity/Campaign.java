package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.axelcrm.entity.enums.CampaignType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;
import com.axelcrm.auth.entity.User;

/**
 * Marketing campaign targeting leads or clients.
 */
@Entity
@Table(name = "campaigns")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Campaign extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignType type = CampaignType.EMAIL;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "recipients_count")
    private Integer recipientsCount = 0;

    @Column(name = "sent_count")
    private Integer sentCount = 0;

    @Column(name = "open_count")
    private Integer openCount = 0;

    @Column(name = "click_count")
    private Integer clickCount = 0;

    @Column(nullable = false)
    private String status = "RASCUNHO";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
