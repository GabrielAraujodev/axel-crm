package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.FinancialTransactionRequest;
import com.axelcrm.dto.FinancialTransactionResponse;
import com.axelcrm.entity.enums.TransactionType;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.FinancialTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FinancialTransactionControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FinancialTransactionService financialTransactionService;

    private final UUID txId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        financialTransactionService = mock(FinancialTransactionService.class);
        FinancialTransactionController controller = new FinancialTransactionController(financialTransactionService);
        var settings = new SpringDataWebSettings(PageSerializationMode.VIA_DTO);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new SpringDataJacksonConfiguration.PageModule(settings));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void findAll_ShouldReturn200() throws Exception {
        var response = new FinancialTransactionResponse(txId, "Venda", TransactionType.INCOME,
                BigDecimal.valueOf(5000), LocalDate.now(), null, null, true, null,
                null, null, null, null, null, null, LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(response));

        when(financialTransactionService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/financial-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Venda"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new FinancialTransactionResponse(txId, "Venda", TransactionType.INCOME,
                BigDecimal.valueOf(5000), LocalDate.now(), null, null, true, null,
                null, null, null, null, null, null, LocalDateTime.now(), null);

        when(financialTransactionService.findById(ORG_ID, txId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/financial-transactions/{id}", txId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value("INCOME"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(financialTransactionService.findById(ORG_ID, txId))
                .thenThrow(new ResourceNotFoundException("FinancialTransaction", "id", txId));

        mockMvc.perform(get("/api/v1/financial-transactions/{id}", txId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new FinancialTransactionRequest(
                "Nova Receita", TransactionType.INCOME, BigDecimal.valueOf(3000),
                LocalDate.now(), null, null, false, null, null, null, null);
        var response = new FinancialTransactionResponse(UUID.randomUUID(), "Nova Receita",
                TransactionType.INCOME, BigDecimal.valueOf(3000), LocalDate.now(), null, null,
                false, null, null, null, null, null, null, null, LocalDateTime.now(), null);

        when(financialTransactionService.create(eq(ORG_ID), any(FinancialTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/financial-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Nova Receita"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var request = new FinancialTransactionRequest(
                "Updated", TransactionType.EXPENSE, BigDecimal.valueOf(1000),
                LocalDate.now(), null, null, false, null, null, null, null);
        var response = new FinancialTransactionResponse(txId, "Updated",
                TransactionType.EXPENSE, BigDecimal.valueOf(1000), LocalDate.now(), null, null,
                false, null, null, null, null, null, null, null, LocalDateTime.now(), null);

        when(financialTransactionService.update(eq(ORG_ID), eq(txId), any(FinancialTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/financial-transactions/{id}", txId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/financial-transactions/{id}", txId))
                .andExpect(status().isNoContent());

        verify(financialTransactionService).delete(ORG_ID, txId);
    }
}
