package com.axelcrm.service;

import com.axelcrm.dto.DealResponse;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.PipelineStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StageTransitionService {

    private final DealRepository dealRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public DealResponse transition(UUID organizationId, UUID userId, UUID dealId, UUID targetStageId, String reason) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        if (Boolean.TRUE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi ganho. Não é possível alterar o estágio.");
        }
        if (Boolean.FALSE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi perdido. Reabra antes de alterar o estágio.");
        }

        PipelineStage targetStage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(targetStageId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("PipelineStage", "id", targetStageId));

        if (!targetStage.getPipeline().getId().equals(deal.getPipeline().getId())) {
            throw new BadRequestException("O estágio destino não pertence ao mesmo pipeline do negócio.");
        }

        List<PipelineStage> pipelineStages = pipelineStageRepository
                .findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(deal.getPipeline().getId(), organizationId)
                .stream()
                .sorted(Comparator.comparingInt(PipelineStage::getPosition))
                .toList();

        int currentPos = deal.getStage().getPosition();
        int targetPos = targetStage.getPosition();

        if (targetPos < currentPos && (reason == null || reason.isBlank())) {
            throw new BadRequestException(
                    "Movimento regressivo no pipeline requer uma justificativa (motivo).");
        }

        String oldStage = deal.getStage().getName();
        UUID oldStageId = deal.getStage().getId();

        deal.setStage(targetStage);
        if (targetPos == pipelineStages.size() - 1) {
            deal.setWon(true);
            deal.setClosedAt(LocalDateTime.now());
        }
        deal = dealRepository.save(deal);

        auditLogService.log(organizationId, userId, "STAGE_TRANSITION", "Deal", dealId.toString(),
                "Stage: " + oldStage,
                "Stage: " + targetStage.getName() + (reason != null ? " | Motivo: " + reason : ""));

        return toResponse(deal);
    }

    @Transactional
    public DealResponse markLost(UUID organizationId, UUID userId, UUID dealId, String reason) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        if (Boolean.TRUE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi ganho. Não é possível marcar como perdido.");
        }

        String oldStage = deal.getStage().getName();

        deal.setWon(false);
        deal.setClosedAt(LocalDateTime.now());
        deal = dealRepository.save(deal);

        auditLogService.log(organizationId, userId, "DEAL_LOST", "Deal", dealId.toString(),
                "Stage: " + oldStage,
                "Perdido" + (reason != null ? " | Motivo: " + reason : ""));

        return toResponse(deal);
    }

    @Transactional
    public DealResponse reopen(UUID organizationId, UUID userId, UUID dealId) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        if (deal.getWon() == null) {
            throw new BadRequestException("Negócio não está fechado. Não é necessário reabrir.");
        }

        deal.setWon(null);
        deal.setClosedAt(null);
        deal = dealRepository.save(deal);

        auditLogService.log(organizationId, userId, "DEAL_REOPENED", "Deal", dealId.toString(),
                "Fechado em: " + deal.getClosedAt(),
                "Reaberto");

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
