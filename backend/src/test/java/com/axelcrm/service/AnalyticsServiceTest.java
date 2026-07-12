package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.analytics.repository.AnalyticsViewRepository;
import com.axelcrm.analytics.repository.AnalyticsViewRepository.*;
import com.axelcrm.dto.LeadAnalyticsResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse;
import com.axelcrm.dto.ProjectProfitabilityResponse;
import com.axelcrm.dto.ProposalAnalyticsResponse;
import com.axelcrm.dto.SalesAnalyticsResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock AnalyticsViewRepository viewRepo;

    @InjectMocks AnalyticsService analyticsService;

    private final UUID orgId = UUID.randomUUID();

    // ── Sales Analytics ────────────────────────────────────────────────

    @Test
    void getSalesAnalytics_ShouldCountWonLostOpen() {
        when(viewRepo.findDealPipeline(orgId)).thenReturn(List.of(
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(1000), true, "Negociação", 50),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(2000), false, "Proposta", 80),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(3000), null, "Negociação", 50),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(4000), null, "Proposta", 80)));

        SalesAnalyticsResponse r = analyticsService.getSalesAnalytics(orgId);

        assertEquals(4, r.totalDeals());
        assertEquals(1, r.wonDeals());
        assertEquals(1, r.lostDeals());
        assertEquals(2, r.openDeals());
    }

    @Test
    void getSalesAnalytics_ShouldCalculateWinRate() {
        when(viewRepo.findDealPipeline(orgId)).thenReturn(List.of(
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.TEN, true, "Stage", 50),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.TEN, false, "Stage", 50),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.TEN, true, "Stage", 50)));

        SalesAnalyticsResponse r = analyticsService.getSalesAnalytics(orgId);
        // 2 won, 1 lost → 66.7%
        assertEquals(0, BigDecimal.valueOf(66.7).compareTo(r.winRate()));
    }

    @Test
    void getSalesAnalytics_ShouldReturnZeroWinRateWhenNoClosedDeals() {
        when(viewRepo.findDealPipeline(orgId))
                .thenReturn(List.of(new DealPipelineRow(UUID.randomUUID(), BigDecimal.TEN, null, "Stage", 50)));

        SalesAnalyticsResponse r = analyticsService.getSalesAnalytics(orgId);
        assertEquals(BigDecimal.ZERO, r.winRate());
    }

    @Test
    void getSalesAnalytics_ShouldCalculatePipelineAndWeighted() {
        when(viewRepo.findDealPipeline(orgId)).thenReturn(List.of(
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(1000), null, "Negociação", 50),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(2000), null, "Proposta", null),
                new DealPipelineRow(UUID.randomUUID(), BigDecimal.valueOf(3000), true, "Negociação", 50)));

        SalesAnalyticsResponse r = analyticsService.getSalesAnalytics(orgId);

        // pipeline = open deals only → 1000 + 2000 = 3000
        assertEquals(0, BigDecimal.valueOf(3000).compareTo(r.totalPipelineValue()));
        // weighted: 1000*0.5 = 500 (s1), 2000*0 = 0 (s2 with null winProb)
        assertEquals(0, BigDecimal.valueOf(500).compareTo(r.weightedPipelineValue()));
    }

    // ── Lead Analytics ─────────────────────────────────────────────────

    @Test
    void getLeadAnalytics_ShouldGroupByStageAndSource() {
        when(viewRepo.findLeadStageCounts(orgId)).thenReturn(List.of(
                new LeadStageCount("NEW", 2),
                new LeadStageCount("CONTACTED", 1),
                new LeadStageCount("CONVERTED", 1)));
        when(viewRepo.findLeadSourceCounts(orgId)).thenReturn(List.of(
                new LeadSourceCount("WEBSITE", 3),
                new LeadSourceCount("REFERRAL", 1)));
        when(viewRepo.countConvertedLeads(orgId)).thenReturn(1L);
        when(viewRepo.countTotalLeads(orgId)).thenReturn(4L);

        LeadAnalyticsResponse r = analyticsService.getLeadAnalytics(orgId);

        assertEquals(4, r.totalLeads());
        assertEquals(1, r.convertedLeads());
        assertEquals(Long.valueOf(2), r.byStage().get("NEW"));
        assertEquals(Long.valueOf(1), r.byStage().get("CONTACTED"));
        assertEquals(Long.valueOf(3), r.bySource().get("WEBSITE"));
    }

    @Test
    void getLeadAnalytics_ShouldCalculateConversionRate() {
        when(viewRepo.findLeadStageCounts(orgId)).thenReturn(List.of(
                new LeadStageCount("CONVERTED", 2),
                new LeadStageCount("NEW", 1),
                new LeadStageCount("CONTACTED", 1)));
        when(viewRepo.findLeadSourceCounts(orgId)).thenReturn(List.of(
                new LeadSourceCount("WEBSITE", 2),
                new LeadSourceCount("REFERRAL", 1),
                new LeadSourceCount("EMAIL", 1)));
        when(viewRepo.countConvertedLeads(orgId)).thenReturn(2L);
        when(viewRepo.countTotalLeads(orgId)).thenReturn(4L);

        LeadAnalyticsResponse r = analyticsService.getLeadAnalytics(orgId);
        // 2 converted / 4 total = 50.0%
        assertEquals(0, BigDecimal.valueOf(50.0).compareTo(r.conversionRate()));
    }

    @Test
    void getLeadAnalytics_ShouldReturnZeroConversionRateWhenNoLeads() {
        when(viewRepo.countTotalLeads(orgId)).thenReturn(0L);
        when(viewRepo.findLeadStageCounts(orgId)).thenReturn(List.of());
        when(viewRepo.findLeadSourceCounts(orgId)).thenReturn(List.of());

        LeadAnalyticsResponse r = analyticsService.getLeadAnalytics(orgId);
        assertEquals(0, r.totalLeads());
        assertEquals(BigDecimal.ZERO, r.conversionRate());
    }

    // ── Financial Trend ────────────────────────────────────────────────

    @Test
    void getFinancialTrend_ShouldGroupByMonth() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 3, 31);

        when(viewRepo.findMonthlyFinancial(orgId, start, end)).thenReturn(List.of(
                new MonthlyFinancialRow(2026, 1, "INCOME", BigDecimal.valueOf(5000), true, LocalDate.of(2026, 1, 15)),
                new MonthlyFinancialRow(2026, 1, "EXPENSE", BigDecimal.valueOf(2000), true, LocalDate.of(2026, 1, 20)),
                new MonthlyFinancialRow(2026, 2, "INCOME", BigDecimal.valueOf(3000), true, LocalDate.of(2026, 2, 10)),
                new MonthlyFinancialRow(2026, 2, "EXPENSE", BigDecimal.valueOf(1000), true, LocalDate.of(2026, 2, 15))));

        MonthlyFinancialTrendResponse r = analyticsService.getFinancialTrend(orgId, start, end);

        assertEquals(YearMonth.of(2026, 1), r.startPeriod());
        assertEquals(YearMonth.of(2026, 3), r.endPeriod());
        assertEquals(2, r.entries().size());

        var jan = r.entries().get(0);
        assertEquals(YearMonth.of(2026, 1), jan.period());
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(jan.revenue()));
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(jan.expenses()));
        assertEquals(0, BigDecimal.valueOf(3000).compareTo(jan.net()));
    }

    @Test
    void getFinancialTrend_ShouldReturnEmptyMonths() {
        var start = LocalDate.of(2026, 1, 1);
        var end = LocalDate.of(2026, 2, 28);

        when(viewRepo.findMonthlyFinancial(orgId, start, end)).thenReturn(List.of());

        MonthlyFinancialTrendResponse r = analyticsService.getFinancialTrend(orgId, start, end);
        assertTrue(r.entries().isEmpty());
    }

    // ── Project Profitability ──────────────────────────────────────────

    @Test
    void getProjectProfitability_ShouldCalculateMargins() {
        when(viewRepo.findProjectProfitability(orgId)).thenReturn(List.of(
                new ProjectProfitRow(UUID.randomUUID(), "Proj A", "IN_PROGRESS",
                        BigDecimal.valueOf(10000), BigDecimal.valueOf(7000), "Cliente A"),
                new ProjectProfitRow(UUID.randomUUID(), "Proj B", "COMPLETED",
                        BigDecimal.valueOf(20000), BigDecimal.valueOf(18000), "Cliente B")));

        ProjectProfitabilityResponse r = analyticsService.getProjectProfitability(orgId);

        assertEquals(2, r.totalProjects());
        assertEquals(0, BigDecimal.valueOf(30000).compareTo(r.totalBudget()));
        assertEquals(0, BigDecimal.valueOf(25000).compareTo(r.totalCost()));
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(r.totalMargin()));
        // avg margin: 5000/30000 = 16.7%
        assertEquals(0, BigDecimal.valueOf(16.7).compareTo(r.avgMarginPercent()));
    }

    @Test
    void getProjectProfitability_ShouldHandleZeroBudget() {
        when(viewRepo.findProjectProfitability(orgId)).thenReturn(List.of(
                new ProjectProfitRow(UUID.randomUUID(), "Proj", "DRAFT",
                        BigDecimal.ZERO, BigDecimal.valueOf(100), null)));

        ProjectProfitabilityResponse r = analyticsService.getProjectProfitability(orgId);
        assertEquals(1, r.totalProjects());
        assertEquals(BigDecimal.ZERO, r.avgMarginPercent());
    }

    // ── Proposal Analytics ─────────────────────────────────────────────

    @Test
    void getProposalAnalytics_ShouldCalculateWinRateAndStatusDistribution() {
        when(viewRepo.findProposalAnalytics(orgId)).thenReturn(List.of(
                new ProposalAnalyticsRow(UUID.randomUUID(), "ACCEPTED", BigDecimal.valueOf(1000)),
                new ProposalAnalyticsRow(UUID.randomUUID(), "ACCEPTED", BigDecimal.valueOf(2000)),
                new ProposalAnalyticsRow(UUID.randomUUID(), "REJECTED", BigDecimal.valueOf(500)),
                new ProposalAnalyticsRow(UUID.randomUUID(), "DRAFT", BigDecimal.valueOf(300))));

        ProposalAnalyticsResponse r = analyticsService.getProposalAnalytics(orgId);

        assertEquals(4, r.totalProposals());
        assertEquals(2, r.acceptedCount());
        assertEquals(1, r.rejectedCount());
        // win rate: 2 / (2+1) = 66.7%
        assertEquals(0, BigDecimal.valueOf(66.7).compareTo(r.winRate()));
        assertEquals(Long.valueOf(2), r.byStatus().get("ACCEPTED"));
        assertEquals(Long.valueOf(1), r.byStatus().get("REJECTED"));
    }

    @Test
    void getProposalAnalytics_ShouldCalculateAverageAmount() {
        when(viewRepo.findProposalAnalytics(orgId)).thenReturn(List.of(
                new ProposalAnalyticsRow(UUID.randomUUID(), "ACCEPTED", BigDecimal.valueOf(1000)),
                new ProposalAnalyticsRow(UUID.randomUUID(), "DRAFT", BigDecimal.valueOf(3000))));

        ProposalAnalyticsResponse r = analyticsService.getProposalAnalytics(orgId);
        // (1000 + 3000) / 2 = 2000.00
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(r.avgAmount()));
    }

    @Test
    void getProposalAnalytics_ShouldReturnZeroWhenEmpty() {
        when(viewRepo.findProposalAnalytics(orgId)).thenReturn(List.of());

        ProposalAnalyticsResponse r = analyticsService.getProposalAnalytics(orgId);
        assertEquals(0, r.totalProposals());
        assertEquals(BigDecimal.ZERO, r.winRate());
        assertTrue(r.byStatus().isEmpty());
    }
}
