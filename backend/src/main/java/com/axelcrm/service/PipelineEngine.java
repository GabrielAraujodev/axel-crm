package com.axelcrm.service;

import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.DealStageHistory;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.DealStageHistoryRepository;
import com.axelcrm.repository.PipelineStageRepository;
import com.axelcrm.auth.entity.User;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PipelineEngine {

    private final DealRepository dealRepository;
    private final DealStageHistoryRepository dealStageHistoryRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public void logInitialStage(UUID organizationId, UUID userId, Deal deal, PipelineStage initialStage) {
        // Double check if there's an existing open history
        dealStageHistoryRepository.findFirstByDeal_IdAndLeftAtIsNullAndDeletedAtIsNull(deal.getId())
                .ifPresent(existing -> {
                    existing.setLeftAt(LocalDateTime.now());
                    existing.setDurationSeconds(ChronoUnit.SECONDS.between(existing.getEnteredAt(), existing.getLeftAt()));
                    dealStageHistoryRepository.save(existing);
                });

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        DealStageHistory history = new DealStageHistory();
        history.setDeal(deal);
        history.setStage(initialStage);
        history.setEnteredAt(LocalDateTime.now());
        history.setPerformedBy(user);
        var org = new com.axelcrm.commons.entity.Organization();
        org.setId(organizationId);
        history.setOrganization(org);

        dealStageHistoryRepository.save(history);
    }

    @Transactional
    public void transitionStage(UUID organizationId, UUID userId, Deal deal, PipelineStage targetStage, String reason) {
        if (Boolean.TRUE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi ganho. Não é possível alterar o estágio.");
        }
        if (Boolean.FALSE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi perdido. Reabra antes de alterar o estágio.");
        }

        if (!targetStage.getPipeline().getId().equals(deal.getPipeline().getId())) {
            throw new BadRequestException("O estágio destino não pertence ao mesmo pipeline do negócio.");
        }

        List<PipelineStage> pipelineStages = pipelineStageRepository
                .findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(deal.getPipeline().getId(), organizationId)
                .stream()
                .sorted(Comparator.comparingInt(PipelineStage::getPosition))
                .toList();

        int currentPos = deal.getStage() != null ? deal.getStage().getPosition() : 0;
        int targetPos = targetStage.getPosition();

        if (targetPos < currentPos && (reason == null || reason.isBlank())) {
            throw new BadRequestException("Movimento regressivo no pipeline requer uma justificativa (motivo).");
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Close current stage history
        dealStageHistoryRepository.findFirstByDeal_IdAndLeftAtIsNullAndDeletedAtIsNull(deal.getId())
                .ifPresent(history -> {
                    history.setLeftAt(now);
                    history.setDurationSeconds(ChronoUnit.SECONDS.between(history.getEnteredAt(), now));
                    dealStageHistoryRepository.save(history);
                });

        // 2. Update deal stage
        String oldStageName = deal.getStage() != null ? deal.getStage().getName() : "N/A";
        deal.setStage(targetStage);
        if (targetPos == pipelineStages.size() - 1) {
            deal.setWon(true);
            deal.setClosedAt(now);
        }
        dealRepository.save(deal);

        // 3. Save new stage history
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        DealStageHistory newHistory = new DealStageHistory();
        newHistory.setDeal(deal);
        newHistory.setStage(targetStage);
        newHistory.setEnteredAt(now);
        newHistory.setTransitionReason(reason);
        newHistory.setPerformedBy(user);
        var org = new com.axelcrm.commons.entity.Organization();
        org.setId(organizationId);
        newHistory.setOrganization(org);
        dealStageHistoryRepository.save(newHistory);

        // 4. Log audit log
        auditLogService.log(organizationId, userId, "STAGE_TRANSITION", "Deal", deal.getId().toString(),
                "Stage: " + oldStageName,
                "Stage: " + targetStage.getName() + (reason != null ? " | Motivo: " + reason : ""));
    }

    @Transactional
    public void markLost(UUID organizationId, UUID userId, Deal deal, String reason) {
        if (Boolean.TRUE.equals(deal.getWon())) {
            throw new BadRequestException("Negócio já foi ganho. Não é possível marcar como perdido.");
        }

        LocalDateTime now = LocalDateTime.now();

        // Close current stage history
        dealStageHistoryRepository.findFirstByDeal_IdAndLeftAtIsNullAndDeletedAtIsNull(deal.getId())
                .ifPresent(history -> {
                    history.setLeftAt(now);
                    history.setDurationSeconds(ChronoUnit.SECONDS.between(history.getEnteredAt(), now));
                    dealStageHistoryRepository.save(history);
                });

        String oldStageName = deal.getStage() != null ? deal.getStage().getName() : "N/A";

        deal.setWon(false);
        deal.setClosedAt(now);
        dealRepository.save(deal);

        auditLogService.log(organizationId, userId, "DEAL_LOST", "Deal", deal.getId().toString(),
                "Stage: " + oldStageName,
                "Perdido" + (reason != null ? " | Motivo: " + reason : ""));
    }

    @Transactional
    public void reopen(UUID organizationId, UUID userId, Deal deal) {
        if (deal.getWon() == null) {
            throw new BadRequestException("Negócio não está fechado. Não é necessário reabrir.");
        }

        LocalDateTime now = LocalDateTime.now();
        deal.setWon(null);
        deal.setClosedAt(null);
        dealRepository.save(deal);

        // Open new history entry for the current stage
        if (deal.getStage() != null) {
            User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
            DealStageHistory history = new DealStageHistory();
            history.setDeal(deal);
            history.setStage(deal.getStage());
            history.setEnteredAt(now);
            history.setPerformedBy(user);
            var org = new com.axelcrm.commons.entity.Organization();
            org.setId(organizationId);
            history.setOrganization(org);
            dealStageHistoryRepository.save(history);
        }

        auditLogService.log(organizationId, userId, "DEAL_REOPENED", "Deal", deal.getId().toString(),
                "Fechado em: " + deal.getClosedAt(),
                "Reaberto");
    }
}
