package com.axelcrm.service;

import com.axelcrm.entity.Lead;
import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadScoringService {

    private final LeadRepository leadRepository;

    @Transactional
    public Lead recalculate(UUID organizationId, UUID leadId) {
        Lead lead = leadRepository.findByIdAndOrganization_Id(leadId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        int score = 0;

        // estimated value: 1 pt per R$ 100
        if (lead.getEstimatedValue() != null && lead.getEstimatedValue().compareTo(BigDecimal.ZERO) > 0) {
            score += lead.getEstimatedValue().divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.DOWN).intValue();
        }

        // source quality
        if (lead.getSource() != null) {
            score += switch (lead.getSource()) {
                case REFERRAL -> 30;
                case WEBSITE -> 20;
                case SOCIAL_MEDIA -> 15;
                case EMAIL -> 10;
                case EVENT -> 25;
                case PHONE -> 5;
                default -> 8;
            };
        }

        // recent contact bonus
        if (lead.getLastContactAt() != null) {
            long daysSinceContact = ChronoUnit.DAYS.between(lead.getLastContactAt(), LocalDateTime.now());
            if (daysSinceContact <= 3) score += 20;
            else if (daysSinceContact <= 7) score += 10;
            else if (daysSinceContact <= 30) score += 5;
        }

        // recency bonus (newer = hotter)
        if (lead.getCreatedAt() != null) {
            long daysSinceCreation = ChronoUnit.DAYS.between(lead.getCreatedAt(), LocalDateTime.now());
            if (daysSinceCreation <= 7) score += 15;
            else if (daysSinceCreation <= 30) score += 10;
            else if (daysSinceCreation <= 90) score += 5;
        }

        score = Math.min(Math.max(score, 0), 100);
        lead.setScore(score);
        return leadRepository.save(lead);
    }
}
