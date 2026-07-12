package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.CashFlowReportResponse;
import com.axelcrm.dto.IncomeStatementResponse;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.service.FinancialReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FinancialReportControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private FinancialReportService financialReportService;

    @BeforeEach
    void setUp() {
        financialReportService = mock(FinancialReportService.class);
        FinancialReportController controller = new FinancialReportController(financialReportService);
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
    void generateCashFlow_ShouldReturn200() throws Exception {
        var response = new CashFlowReportResponse(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 31),
                List.of(),
                BigDecimal.valueOf(8000),
                BigDecimal.valueOf(3000),
                BigDecimal.valueOf(5000));

        when(financialReportService.generateCashFlow(eq(ORG_ID), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/financial-reports/cash-flow")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInflows").value(8000))
                .andExpect(jsonPath("$.totalOutflows").value(3000))
                .andExpect(jsonPath("$.netCashFlow").value(5000));
    }

    @Test
    void generateIncomeStatement_ShouldReturn200() throws Exception {
        var response = new IncomeStatementResponse(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                BigDecimal.valueOf(15000),
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(10000));

        when(financialReportService.generateIncomeStatement(eq(ORG_ID), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/financial-reports/income-statement")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(15000))
                .andExpect(jsonPath("$.netResult").value(10000));
    }
}
