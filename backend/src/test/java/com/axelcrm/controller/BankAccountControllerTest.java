package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.BankAccountRequest;
import com.axelcrm.dto.BankAccountResponse;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.BankAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
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

class BankAccountControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BankAccountService bankAccountService;

    private final UUID accountId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        bankAccountService = mock(BankAccountService.class);
        BankAccountController controller = new BankAccountController(bankAccountService);
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
        var response = new BankAccountResponse(accountId, "Conta Principal", "Banco do Brasil",
                "12345-6", "0001", BigDecimal.valueOf(10000), true, LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(response));

        when(bankAccountService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/bank-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Conta Principal"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new BankAccountResponse(accountId, "Conta Principal", "Banco do Brasil",
                "12345-6", "0001", BigDecimal.valueOf(10000), true, LocalDateTime.now(), null);

        when(bankAccountService.findById(ORG_ID, accountId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/bank-accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankName").value("Banco do Brasil"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(bankAccountService.findById(ORG_ID, accountId))
                .thenThrow(new ResourceNotFoundException("BankAccount", "id", accountId));

        mockMvc.perform(get("/api/v1/bank-accounts/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new BankAccountRequest("Nova Conta", "Itaú", "67890-1", "0002",
                BigDecimal.valueOf(5000), true);
        var response = new BankAccountResponse(UUID.randomUUID(), "Nova Conta", "Itaú",
                "67890-1", "0002", BigDecimal.valueOf(5000), true, LocalDateTime.now(), null);

        when(bankAccountService.create(eq(ORG_ID), any(BankAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bank-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nova Conta"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var request = new BankAccountRequest("Updated", "Bradesco", "11111-2", "0003",
                BigDecimal.valueOf(8000), true);
        var response = new BankAccountResponse(accountId, "Updated", "Bradesco",
                "11111-2", "0003", BigDecimal.valueOf(8000), true, LocalDateTime.now(), null);

        when(bankAccountService.update(eq(ORG_ID), eq(accountId), any(BankAccountRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/bank-accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/bank-accounts/{id}", accountId))
                .andExpect(status().isNoContent());

        verify(bankAccountService).delete(ORG_ID, accountId);
    }
}
