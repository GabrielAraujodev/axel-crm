package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.ProposalRequest;
import com.axelcrm.dto.ProposalResponse;
import com.axelcrm.entity.enums.ProposalStatus;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.ProposalService;
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

class ProposalControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProposalService proposalService;

    private final UUID proposalId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        proposalService = mock(ProposalService.class);
        ProposalController controller = new ProposalController(proposalService);
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
        var proposal = new ProposalResponse(proposalId, "Website Proposal", null,
                ProposalStatus.DRAFT, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1),
                new BigDecimal("15000"), BigDecimal.ZERO, null,
                null, null, List.of(), LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(proposal));

        when(proposalService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Website Proposal"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new ProposalResponse(proposalId, "Website Proposal", null,
                ProposalStatus.DRAFT, null, null, null, null, null,
                null, null, List.of(), LocalDateTime.now(), null);

        when(proposalService.findById(ORG_ID, proposalId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/proposals/{id}", proposalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Website Proposal"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(proposalService.findById(ORG_ID, proposalId))
                .thenThrow(new ResourceNotFoundException("Proposal", "id", proposalId));

        mockMvc.perform(get("/api/v1/proposals/{id}", proposalId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new ProposalRequest("New Proposal", "Desc",
                ProposalStatus.DRAFT, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1),
                BigDecimal.ZERO, clientId, null, null, null);
        var response = new ProposalResponse(UUID.randomUUID(), "New Proposal", "Desc",
                ProposalStatus.DRAFT, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                null, null, List.of(), LocalDateTime.now(), null);

        when(proposalService.create(eq(ORG_ID), any(ProposalRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Proposal"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        ProposalRequest request = new ProposalRequest("Revised Title", "Revised Description", ProposalStatus.SENT, LocalDate.now(), LocalDate.now().plusDays(10), BigDecimal.valueOf(100), clientId, null, null, null);
        var response = new ProposalResponse(proposalId, "Updated", "Updated desc",
                ProposalStatus.SENT, null, null, BigDecimal.ZERO, BigDecimal.ZERO, null,
                null, null, List.of(), LocalDateTime.now(), null);

        when(proposalService.update(eq(ORG_ID), eq(proposalId), any(ProposalRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/proposals/{id}", proposalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/proposals/{id}", proposalId))
                .andExpect(status().isNoContent());

        verify(proposalService).delete(ORG_ID, proposalId);
    }
}
