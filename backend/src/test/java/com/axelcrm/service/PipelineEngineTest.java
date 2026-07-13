package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.DealStageHistory;
import com.axelcrm.entity.Pipeline;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.DealStageHistoryRepository;
import com.axelcrm.repository.PipelineStageRepository;
import com.axelcrm.auth.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PipelineEngineTest {

    @Mock
    DealRepository dealRepository;

    @Mock
    DealStageHistoryRepository dealStageHistoryRepository;

    @Mock
    PipelineStageRepository pipelineStageRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AuditLogService auditLogService;

    @InjectMocks
    PipelineEngine pipelineEngine;

    private final UUID orgId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void transitionStage_ShouldSucceedProgressive() {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(UUID.randomUUID());

        PipelineStage currentStage = new PipelineStage();
        currentStage.setId(UUID.randomUUID());
        currentStage.setPipeline(pipeline);
        currentStage.setPosition(0);
        currentStage.setName("Start");

        PipelineStage targetStage = new PipelineStage();
        targetStage.setId(UUID.randomUUID());
        targetStage.setPipeline(pipeline);
        targetStage.setPosition(1);
        targetStage.setName("Middle");

        PipelineStage finalStage = new PipelineStage();
        finalStage.setId(UUID.randomUUID());
        finalStage.setPipeline(pipeline);
        finalStage.setPosition(2);
        finalStage.setName("Final");

        Deal deal = new Deal();
        deal.setId(UUID.randomUUID());
        deal.setPipeline(pipeline);
        deal.setStage(currentStage);

        DealStageHistory currentHistory = new DealStageHistory();
        currentHistory.setEnteredAt(LocalDateTime.now().minusHours(2));

        when(pipelineStageRepository.findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(any(), any()))
                .thenReturn(List.of(currentStage, targetStage, finalStage));
        when(dealStageHistoryRepository.findFirstByDeal_IdAndLeftAtIsNullAndDeletedAtIsNull(any()))
                .thenReturn(Optional.of(currentHistory));

        pipelineEngine.transitionStage(orgId, userId, deal, targetStage, null);

        assertEquals(targetStage, deal.getStage());
        assertNull(deal.getWon());
        verify(dealRepository).save(deal);
        verify(dealStageHistoryRepository, times(2)).save(any()); // Save closed history & new history
    }

    @Test
    void transitionStage_ShouldRequireReasonOnRegressive() {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(UUID.randomUUID());

        PipelineStage currentStage = new PipelineStage();
        currentStage.setId(UUID.randomUUID());
        currentStage.setPipeline(pipeline);
        currentStage.setPosition(2);

        PipelineStage targetStage = new PipelineStage();
        targetStage.setId(UUID.randomUUID());
        targetStage.setPipeline(pipeline);
        targetStage.setPosition(1);

        Deal deal = new Deal();
        deal.setId(UUID.randomUUID());
        deal.setPipeline(pipeline);
        deal.setStage(currentStage);

        when(pipelineStageRepository.findByPipeline_IdAndOrganization_IdAndDeletedAtIsNull(any(), any()))
                .thenReturn(List.of(targetStage, currentStage));

        assertThrows(BadRequestException.class, () ->
                pipelineEngine.transitionStage(orgId, userId, deal, targetStage, null)
        );
    }
}
