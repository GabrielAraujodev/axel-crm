package com.axelcrm.service;

import com.axelcrm.analytics.repository.AnalyticsViewRepository;
import com.axelcrm.analytics.repository.AnalyticsViewRepository.*;
import com.axelcrm.dto.DashboardSummaryResponse;
import com.axelcrm.dto.DashboardCountsResponse;
import com.axelcrm.dto.LeadAnalyticsResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse.MonthlyFinanceEntry;
import com.axelcrm.dto.ProjectProfitabilityResponse;
import com.axelcrm.dto.ProjectProfitabilityResponse.ProjectProfitEntry;
import com.axelcrm.dto.ProposalAnalyticsResponse;
import com.axelcrm.dto.SalesAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsViewRepository viewRepo;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(UUID organizationId) {
        EntityCounts counts = viewRepo.findEntityCounts(organizationId);

        Map<String, Long> leadsByStage = viewRepo.findLeadStageCounts(organizationId)
                .stream()
                .collect(Collectors.toMap(LeadStageCount::stage, LeadStageCount::count));

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<MonthlyFinancialRow> monthTx = viewRepo.findMonthlyFinancial(organizationId, startOfMonth, endOfMonth);
        BigDecimal monthlyRevenue = monthTx.stream()
                .filter(t -> "INCOME".equals(t.type()) && t.paid() && t.amount() != null)
                .map(MonthlyFinancialRow::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthlyExpenses = monthTx.stream()
                .filter(t -> "EXPENSE".equals(t.type()) && t.paid() && t.amount() != null)
                .map(MonthlyFinancialRow::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardSummaryResponse(
                counts.totalLeads(),
                leadsByStage,
                counts.totalClients(),
                counts.totalDeals(),
                counts.openDeals(),
                counts.wonDeals(),
                counts.pipelineValue(),
                monthlyRevenue,
                monthlyExpenses,
                counts.tasksPending(),
                counts.tasksCompleted()
        );
    }

    @Transactional(readOnly = true)
    public DashboardCountsResponse getDashboardCounts(UUID organizationId) {
        EntityCounts counts = viewRepo.findEntityCounts(organizationId);
        return new DashboardCountsResponse(
                counts.totalClients(),
                counts.totalLeads(),
                counts.totalDeals(),
                counts.totalProjects(),
                counts.tasksPending() + counts.tasksCompleted(),
                counts.totalProposals(),
                counts.totalCampaigns(),
                counts.totalSupportTickets()
        );
    }

    @Transactional(readOnly = true)
    public SalesAnalyticsResponse getSalesAnalytics(UUID organizationId) {
        List<DealPipelineRow> deals = viewRepo.findDealPipeline(organizationId);
        long total = deals.size();
        long won = deals.stream().filter(d -> Boolean.TRUE.equals(d.won())).count();
        long lost = deals.stream().filter(d -> Boolean.FALSE.equals(d.won())).count();
        long open = deals.stream().filter(d -> d.won() == null).count();

        BigDecimal pipelineValue = deals.stream()
                .filter(d -> d.won() == null && d.value() != null)
                .map(DealPipelineRow::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal weightedPipeline = deals.stream()
                .filter(d -> d.won() == null && d.value() != null && d.winProbability() != null)
                .map(d -> d.value().multiply(
                        BigDecimal.valueOf(d.winProbability()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal winRate = won + lost > 0
                ? BigDecimal.valueOf(won).divide(BigDecimal.valueOf(won + lost), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal avgDeal = total > 0
                ? deals.stream().filter(d -> d.value() != null).map(DealPipelineRow::value)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, BigDecimal> byStage = new LinkedHashMap<>();
        deals.stream()
                .filter(d -> d.won() == null && d.stageName() != null)
                .collect(Collectors.groupingBy(DealPipelineRow::stageName,
                        Collectors.reducing(BigDecimal.ZERO, d -> d.value() != null ? d.value() : BigDecimal.ZERO, BigDecimal::add)))
                .forEach((stage, val) -> byStage.put(stage, val));

        return new SalesAnalyticsResponse(total, won, lost, open, winRate, pipelineValue, weightedPipeline, avgDeal, byStage);
    }

    @Transactional(readOnly = true)
    public LeadAnalyticsResponse getLeadAnalytics(UUID organizationId) {
        long total = viewRepo.countTotalLeads(organizationId);

        Map<String, Long> byStage = viewRepo.findLeadStageCounts(organizationId)
                .stream()
                .collect(Collectors.toMap(LeadStageCount::stage, LeadStageCount::count));
        Map<String, Long> bySource = viewRepo.findLeadSourceCounts(organizationId)
                .stream()
                .collect(Collectors.toMap(LeadSourceCount::source, LeadSourceCount::count));

        long converted = viewRepo.countConvertedLeads(organizationId);

        BigDecimal conversionRate = total > 0
                ? BigDecimal.valueOf(converted).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new LeadAnalyticsResponse(total, byStage, bySource, converted, conversionRate);
    }

    @Transactional(readOnly = true)
    public MonthlyFinancialTrendResponse getFinancialTrend(UUID organizationId, LocalDate start, LocalDate end) {
        List<MonthlyFinancialRow> all = viewRepo.findMonthlyFinancial(organizationId, start, end);

        Map<YearMonth, List<MonthlyFinancialRow>> byMonth = all.stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.of(t.year(), t.month()),
                        TreeMap::new,
                        Collectors.toList()));

        List<MonthlyFinanceEntry> entries = new ArrayList<>();
        for (var entry : byMonth.entrySet()) {
            YearMonth period = entry.getKey();
            BigDecimal revenue = entry.getValue().stream()
                    .filter(t -> "INCOME".equals(t.type()) && t.amount() != null)
                    .map(MonthlyFinancialRow::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal expenses = entry.getValue().stream()
                    .filter(t -> "EXPENSE".equals(t.type()) && t.amount() != null)
                    .map(MonthlyFinancialRow::amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            entries.add(new MonthlyFinanceEntry(period, revenue, expenses, revenue.subtract(expenses)));
        }

        YearMonth startPeriod = start != null ? YearMonth.from(start) : null;
        YearMonth endPeriod = end != null ? YearMonth.from(end) : null;
        return new MonthlyFinancialTrendResponse(startPeriod, endPeriod, entries);
    }

    @Transactional(readOnly = true)
    public ProjectProfitabilityResponse getProjectProfitability(UUID organizationId) {
        List<ProjectProfitRow> projects = viewRepo.findProjectProfitability(organizationId);
        long total = projects.size();

        List<ProjectProfitEntry> entries = projects.stream()
                .filter(p -> p.budget() != null)
                .map(p -> {
                    BigDecimal cost = p.cost() != null ? p.cost() : BigDecimal.ZERO;
                    BigDecimal margin = p.budget().subtract(cost);
                    BigDecimal marginPct = p.budget().compareTo(BigDecimal.ZERO) > 0
                            ? margin.divide(p.budget(), 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    return new ProjectProfitEntry(p.id(), p.name(), p.status(), p.clientName(),
                            p.budget(), cost, margin, marginPct);
                })
                .toList();

        BigDecimal totalBudget = entries.stream()
                .map(ProjectProfitEntry::budget).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = entries.stream()
                .map(ProjectProfitEntry::cost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalMargin = totalBudget.subtract(totalCost);
        BigDecimal avgMarginPct = total > 0
                ? totalBudget.compareTo(BigDecimal.ZERO) > 0
                        ? totalMargin.divide(totalBudget, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO
                : BigDecimal.ZERO;

        return new ProjectProfitabilityResponse(total, totalBudget, totalCost, totalMargin, avgMarginPct, entries);
    }

    @Transactional(readOnly = true)
    public ProposalAnalyticsResponse getProposalAnalytics(UUID organizationId) {
        List<ProposalAnalyticsRow> proposals = viewRepo.findProposalAnalytics(organizationId);
        long total = proposals.size();

        Map<String, Long> byStatus = proposals.stream()
                .filter(p -> p.status() != null)
                .collect(Collectors.groupingBy(ProposalAnalyticsRow::status, Collectors.counting()));

        long accepted = proposals.stream().filter(p -> "ACCEPTED".equals(p.status())).count();
        long rejected = proposals.stream().filter(p -> "REJECTED".equals(p.status())).count();

        BigDecimal winRate = accepted + rejected > 0
                ? BigDecimal.valueOf(accepted).divide(BigDecimal.valueOf(accepted + rejected), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal avgAmount = total > 0
                ? proposals.stream()
                        .filter(p -> p.totalAmount() != null)
                        .map(ProposalAnalyticsRow::totalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new ProposalAnalyticsResponse(total, byStatus, accepted, rejected, winRate, avgAmount);
    }
}
