package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.DashboardCountsResponse;
import com.axelcrm.dto.DashboardSummaryResponse;
import com.axelcrm.dto.LeadAnalyticsResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse;
import com.axelcrm.dto.MonthlyFinancialTrendResponse.MonthlyFinanceEntry;
import com.axelcrm.dto.ProjectProfitabilityResponse;
import com.axelcrm.dto.ProjectProfitabilityResponse.ProjectProfitEntry;
import com.axelcrm.dto.ProposalAnalyticsResponse;
import com.axelcrm.dto.SalesAnalyticsResponse;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.service.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DashboardControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = mock(AnalyticsService.class);
        DashboardController controller = new DashboardController(analyticsService);
        var settings = new SpringDataWebSettings(PageSerializationMode.VIA_DTO);
        var objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new SpringDataJacksonConfiguration.PageModule(settings));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getSalesAnalytics_ShouldReturn200() throws Exception {
        var response = new SalesAnalyticsResponse(10, 3, 2, 5,
                BigDecimal.valueOf(60.0), BigDecimal.valueOf(50000),
                BigDecimal.valueOf(25000), BigDecimal.valueOf(5000),
                Map.of("Negociação", BigDecimal.valueOf(30000)));

        when(analyticsService.getSalesAnalytics(ORG_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeals").value(10))
                .andExpect(jsonPath("$.wonDeals").value(3))
                .andExpect(jsonPath("$.openDeals").value(5))
                .andExpect(jsonPath("$.totalPipelineValue").value(50000));
    }

    @Test
    void getLeadAnalytics_ShouldReturn200() throws Exception {
        var response = new LeadAnalyticsResponse(20,
                Map.of("NEW", 10L, "CONTACTED", 8L, "CONVERTED", 2L),
                Map.of("WEBSITE", 12L, "REFERRAL", 8L),
                2, BigDecimal.valueOf(10.0));

        when(analyticsService.getLeadAnalytics(ORG_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLeads").value(20))
                .andExpect(jsonPath("$.convertedLeads").value(2))
                .andExpect(jsonPath("$.byStage.NEW").value(10));
    }

    @Test
    void getFinancialTrend_ShouldReturn200() throws Exception {
        var response = new MonthlyFinancialTrendResponse(
                YearMonth.of(2026, 1), YearMonth.of(2026, 3),
                List.of(
                        new MonthlyFinanceEntry(YearMonth.of(2026, 1),
                                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), BigDecimal.valueOf(3000)),
                        new MonthlyFinanceEntry(YearMonth.of(2026, 2),
                                BigDecimal.valueOf(3000), BigDecimal.valueOf(1000), BigDecimal.valueOf(2000))));

        when(analyticsService.getFinancialTrend(eq(ORG_ID), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/financial-trend")
                        .param("start", "2026-01-01")
                        .param("end", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(2))
                .andExpect(jsonPath("$.entries[0].revenue").value(5000));
    }

    @Test
    void getProjectProfitability_ShouldReturn200() throws Exception {
        var response = new ProjectProfitabilityResponse(
                2, BigDecimal.valueOf(30000), BigDecimal.valueOf(25000),
                BigDecimal.valueOf(5000), BigDecimal.valueOf(16.7),
                List.of(
                        new ProjectProfitEntry(UUID.randomUUID(), "Proj A", "IN_PROGRESS", null,
                                BigDecimal.valueOf(10000), BigDecimal.valueOf(7000),
                                BigDecimal.valueOf(3000), BigDecimal.valueOf(30.0))));

        when(analyticsService.getProjectProfitability(ORG_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjects").value(2))
                .andExpect(jsonPath("$.totalBudget").value(30000));
    }

    @Test
    void getProposalAnalytics_ShouldReturn200() throws Exception {
        var response = new ProposalAnalyticsResponse(4,
                Map.of("ACCEPTED", 2L, "REJECTED", 1L, "DRAFT", 1L),
                2, 1, BigDecimal.valueOf(66.7), BigDecimal.valueOf(950.00));

        when(analyticsService.getProposalAnalytics(ORG_ID)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProposals").value(4))
                .andExpect(jsonPath("$.acceptedCount").value(2))
                .andExpect(jsonPath("$.byStatus.ACCEPTED").value(2));
    }
}
