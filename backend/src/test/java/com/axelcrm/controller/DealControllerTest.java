package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.DealRequest;
import com.axelcrm.dto.DealResponse;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.DealService;
import com.axelcrm.service.ProjectService;
import com.axelcrm.service.StageTransitionService;
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

class DealControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DealService dealService;
    private ProjectService projectService;
    private StageTransitionService stageTransitionService;

    private final UUID dealId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        dealService = mock(DealService.class);
        projectService = mock(ProjectService.class);
        stageTransitionService = mock(StageTransitionService.class);
        DealController controller = new DealController(dealService, projectService, stageTransitionService);
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
        var deal = new DealResponse(dealId, "Website", "Build a site",
                new BigDecimal("50000"), UUID.randomUUID(), "Sales",
                UUID.randomUUID(), "Negotiation", UUID.randomUUID(), "Acme",
                null, UUID.randomUUID(), "Alice",
                LocalDate.of(2026, 7, 1), null, null,
                LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(deal));

        when(dealService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Website"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new DealResponse(dealId, "Website", null,
                null, UUID.randomUUID(), "Sales",
                UUID.randomUUID(), "Negotiation", UUID.randomUUID(), "Acme",
                null, null, null, null, null, null,
                LocalDateTime.now(), null);

        when(dealService.findById(ORG_ID, dealId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/deals/{id}", dealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Website"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(dealService.findById(ORG_ID, dealId))
                .thenThrow(new ResourceNotFoundException("Deal", "id", dealId));

        mockMvc.perform(get("/api/v1/deals/{id}", dealId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new DealRequest("New Deal", "Desc", new BigDecimal("30000"),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null,
                UUID.randomUUID(), null);
        var response = new DealResponse(UUID.randomUUID(), "New Deal", "Desc",
                new BigDecimal("30000"), UUID.randomUUID(), "Sales",
                UUID.randomUUID(), "Proposal", UUID.randomUUID(), "Acme",
                null, UUID.randomUUID(), "Alice", null, null, null,
                LocalDateTime.now(), null);

        when(dealService.create(eq(ORG_ID), any(DealRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Deal"));
    }

    @Test
    void convertToProject_ShouldReturn200() throws Exception {
        var projectResponse = new ProjectResponse(UUID.randomUUID(), "Website Proposal", null,
                null, null, new BigDecimal("50000"), null,
                "PLANEJAMENTO", UUID.randomUUID(), null, LocalDateTime.now(), null);

        when(projectService.createFromDeal(ORG_ID, dealId)).thenReturn(projectResponse);

        mockMvc.perform(post("/api/v1/deals/{id}/convert-to-project", dealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Website Proposal"))
                .andExpect(jsonPath("$.status").value("PLANEJAMENTO"));
    }

    @Test
    void convertToProject_ShouldReturn400WhenDealNotWon() throws Exception {
        when(projectService.createFromDeal(ORG_ID, dealId))
                .thenThrow(new BadRequestException("Deal must be won before converting to a project"));

        mockMvc.perform(post("/api/v1/deals/{id}/convert-to-project", dealId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void convertToProject_ShouldReturn404WhenDealNotFound() throws Exception {
        when(projectService.createFromDeal(ORG_ID, dealId))
                .thenThrow(new ResourceNotFoundException("Deal", "id", dealId));

        mockMvc.perform(post("/api/v1/deals/{id}/convert-to-project", dealId))
                .andExpect(status().isNotFound());
    }
}
