package com.axelcrm.analytics.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Repository
public class AnalyticsViewRepository {

    private final JdbcTemplate jdbc;

    public AnalyticsViewRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public EntityCounts findEntityCounts(UUID organizationId) {
        return jdbc.queryForObject(
            """
            SELECT total_leads, total_clients, total_deals, open_deals, won_deals,
                   pipeline_value, tasks_pending, tasks_completed,
                   total_projects, total_proposals, total_campaigns, total_support_tickets
            FROM vw_entity_counts
            WHERE organization_id = ?
            """,
            (rs, row) -> new EntityCounts(
                rs.getLong("total_leads"),
                rs.getLong("total_clients"),
                rs.getLong("total_deals"),
                rs.getLong("open_deals"),
                rs.getLong("won_deals"),
                rs.getBigDecimal("pipeline_value"),
                rs.getLong("tasks_pending"),
                rs.getLong("tasks_completed"),
                rs.getLong("total_projects"),
                rs.getLong("total_proposals"),
                rs.getLong("total_campaigns"),
                rs.getLong("total_support_tickets")
            ),
            organizationId
        );
    }

    public List<LeadStageCount> findLeadStageCounts(UUID organizationId) {
        return jdbc.query(
            """
            SELECT stage, COUNT(*) AS cnt
            FROM vw_lead_analytics
            WHERE organization_id = ? AND stage IS NOT NULL
            GROUP BY stage
            """,
            (rs, row) -> new LeadStageCount(rs.getString("stage"), rs.getLong("cnt")),
            organizationId
        );
    }

    public List<LeadSourceCount> findLeadSourceCounts(UUID organizationId) {
        return jdbc.query(
            """
            SELECT source, COUNT(*) AS cnt
            FROM vw_lead_analytics
            WHERE organization_id = ? AND source IS NOT NULL
            GROUP BY source
            """,
            (rs, row) -> new LeadSourceCount(rs.getString("source"), rs.getLong("cnt")),
            organizationId
        );
    }

    public long countConvertedLeads(UUID organizationId) {
        var result = jdbc.queryForObject(
            "SELECT COUNT(*) FROM vw_lead_analytics WHERE organization_id = ? AND is_converted = 1",
            Long.class,
            organizationId
        );
        return result != null ? result : 0L;
    }

    public long countTotalLeads(UUID organizationId) {
        var result = jdbc.queryForObject(
            "SELECT COUNT(*) FROM vw_lead_analytics WHERE organization_id = ?",
            Long.class,
            organizationId
        );
        return result != null ? result : 0L;
    }

    public List<DealPipelineRow> findDealPipeline(UUID organizationId) {
        return jdbc.query(
            """
            SELECT id, value, won, stage_name, win_probability
            FROM vw_deal_pipeline
            WHERE organization_id = ?
            """,
            (rs, row) -> new DealPipelineRow(
                UUID.fromString(rs.getString("id")),
                rs.getBigDecimal("value"),
                rs.getObject("won", Boolean.class),
                rs.getString("stage_name"),
                rs.getObject("win_probability", Integer.class)
            ),
            organizationId
        );
    }

    public List<MonthlyFinancialRow> findMonthlyFinancial(UUID organizationId, LocalDate start, LocalDate end) {
        return jdbc.query(
            """
            SELECT year, month, type, amount, paid, transaction_date
            FROM vw_monthly_financial
            WHERE organization_id = ?
              AND transaction_date BETWEEN ? AND ?
            """,
            (rs, row) -> new MonthlyFinancialRow(
                rs.getInt("year"),
                rs.getInt("month"),
                rs.getString("type"),
                rs.getBigDecimal("amount"),
                rs.getBoolean("paid"),
                rs.getDate("transaction_date").toLocalDate()
            ),
            organizationId, start, end
        );
    }

    public List<ProjectProfitRow> findProjectProfitability(UUID organizationId) {
        return jdbc.query(
            """
            SELECT id, name, status, budget, cost, client_name
            FROM vw_project_profitability
            WHERE organization_id = ?
            """,
            (rs, row) -> new ProjectProfitRow(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getString("status"),
                rs.getBigDecimal("budget"),
                rs.getBigDecimal("cost"),
                rs.getString("client_name")
            ),
            organizationId
        );
    }

    public List<ProposalAnalyticsRow> findProposalAnalytics(UUID organizationId) {
        return jdbc.query(
            """
            SELECT id, status, total_amount
            FROM vw_proposal_analytics
            WHERE organization_id = ?
            """,
            (rs, row) -> new ProposalAnalyticsRow(
                UUID.fromString(rs.getString("id")),
                rs.getString("status"),
                rs.getBigDecimal("total_amount")
            ),
            organizationId
        );
    }

    // ── Projection records ──────────────────────────────────────────

    public record EntityCounts(
        long totalLeads, long totalClients, long totalDeals,
        long openDeals, long wonDeals, BigDecimal pipelineValue,
        long tasksPending, long tasksCompleted,
        long totalProjects, long totalProposals,
        long totalCampaigns, long totalSupportTickets
    ) {}

    public record LeadStageCount(String stage, long count) {}
    public record LeadSourceCount(String source, long count) {}

    public record DealPipelineRow(
        UUID id, BigDecimal value, Boolean won,
        String stageName, Integer winProbability
    ) {}

    public record MonthlyFinancialRow(
        int year, int month, String type,
        BigDecimal amount, boolean paid, LocalDate transactionDate
    ) {}

    public record ProjectProfitRow(
        UUID id, String name, String status,
        BigDecimal budget, BigDecimal cost, String clientName
    ) {}

    public record ProposalAnalyticsRow(
        UUID id, String status, BigDecimal totalAmount
    ) {}
}
