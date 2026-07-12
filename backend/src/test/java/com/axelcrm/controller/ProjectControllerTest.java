package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.ProjectRequest;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.ProjectService;
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

class ProjectControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ProjectService projectService;

    private final UUID projectId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        projectService = mock(ProjectService.class);
        ProjectController controller = new ProjectController(projectService);
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
        var project = new ProjectResponse(projectId, "Website", "Redesign",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30),
                new BigDecimal("50000"), null, "EM_ANDAMENTO",
                UUID.randomUUID(), null, LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(project));

        when(projectService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Website"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new ProjectResponse(projectId, "Website", "Redesign",
                null, null, null, null, "PLANEJAMENTO",
                UUID.randomUUID(), null, LocalDateTime.now(), null);

        when(projectService.findById(ORG_ID, projectId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Website"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(projectService.findById(ORG_ID, projectId))
                .thenThrow(new ResourceNotFoundException("Project", "id", projectId));

        mockMvc.perform(get("/api/v1/projects/{id}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new ProjectRequest("New Project", "Desc",
                LocalDate.of(2026, 3, 1), null, new BigDecimal("100000"), null,
                null, UUID.randomUUID(), null);
        var response = new ProjectResponse(UUID.randomUUID(), "New Project", "Desc",
                LocalDate.of(2026, 3, 1), null, new BigDecimal("100000"), null,
                "PLANEJAMENTO", UUID.randomUUID(), null, LocalDateTime.now(), null);

        when(projectService.create(eq(ORG_ID), any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var request = new ProjectRequest("Updated", null, null, null,
                new BigDecimal("75000"), new BigDecimal("60000"), "CONCLUIDO",
                UUID.randomUUID(), null);
        var response = new ProjectResponse(projectId, "Updated", null,
                null, null, new BigDecimal("75000"), new BigDecimal("60000"),
                "CONCLUIDO", UUID.randomUUID(), null, LocalDateTime.now(), null);

        when(projectService.update(eq(ORG_ID), eq(projectId), any(ProjectRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        verify(projectService).delete(ORG_ID, projectId);
    }
}
