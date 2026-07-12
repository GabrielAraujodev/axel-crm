package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.dto.ClientRequest;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

class ClientControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ClientService clientService;

    private final UUID clientId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        ClientController controller = new ClientController(clientService);
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
        var client = new ClientResponse(clientId, "Acme", "a@acme.com", null, null,
                null, null, null, null, null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null,
                LocalDateTime.now(), null);
        var page = new PageImpl<>(List.of(client));

        when(clientService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Acme"));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var response = new ClientResponse(clientId, "Acme Corp", "contact@acme.com", null, null,
                null, null, null, null, null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null, LocalDateTime.now(), null);

        when(clientService.findById(ORG_ID, clientId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("contact@acme.com"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(clientService.findById(ORG_ID, clientId))
                .thenThrow(new ResourceNotFoundException("Client", "id", clientId));

        mockMvc.perform(get("/api/v1/clients/{id}", clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new ClientRequest("NewCo", "info@newco.com", null, null, null,
                null, null, null, null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null);
        var response = new ClientResponse(UUID.randomUUID(), "NewCo", "info@newco.com",
                null, null, null, null, null, null, null, null, null, null, null,
                true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null, LocalDateTime.now(), null);

        when(clientService.create(eq(ORG_ID), any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewCo"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var request = new ClientRequest("Updated", "new@client.com", null, null, null, null, null, null, null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null);
        var response = new ClientResponse(clientId, "Updated", "new@client.com", null, null,
                null, null, null, null, null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null, LocalDateTime.now(), null);

        when(clientService.update(eq(ORG_ID), eq(clientId), any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/clients/{id}", clientId))
                .andExpect(status().isNoContent());

        verify(clientService).delete(ORG_ID, clientId);
    }
}
