package com.axelcrm.service;

import com.axelcrm.dto.DealResponse;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StageTransitionService {

    private final DealRepository dealRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final PipelineEngine pipelineEngine;

    @Transactional
    public DealResponse transition(UUID organizationId, UUID userId, UUID dealId, UUID targetStageId, String reason) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        PipelineStage targetStage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(targetStageId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", targetStageId));

        pipelineEngine.transitionStage(organizationId, userId, deal, targetStage, reason);

        return toResponse(deal);
    }

    @Transactional
    public DealResponse markLost(UUID organizationId, UUID userId, UUID dealId, String reason) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        pipelineEngine.markLost(organizationId, userId, deal, reason);

        return toResponse(deal);
    }

    @Transactional
    public DealResponse reopen(UUID organizationId, UUID userId, UUID dealId) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        pipelineEngine.reopen(organizationId, userId, deal);

        return toResponse(deal);
    }

    private DealResponse toResponse(Deal deal) {
        return new DealResponse(
                deal.getId(), deal.getTitle(), deal.getDescription(), deal.getValue(),
                deal.getPipeline() != null ? deal.getPipeline().getId() : null,
                deal.getPipeline() != null ? deal.getPipeline().getName() : null,
                deal.getStage() != null ? deal.getStage().getId() : null,
                deal.getStage() != null ? deal.getStage().getName() : null,
                deal.getClient() != null ? deal.getClient().getId() : null,
                deal.getClient() != null ? deal.getClient().getName() : null,
                deal.getContact() != null ? deal.getContact().getId() : null,
                deal.getAssignedTo() != null ? deal.getAssignedTo().getId() : null,
                deal.getAssignedTo() != null ? deal.getAssignedTo().getName() : null,
                deal.getExpectedCloseDate(), deal.getClosedAt(), deal.getWon(),
                deal.getCreatedAt(), deal.getUpdatedAt()
        );
    }
}
