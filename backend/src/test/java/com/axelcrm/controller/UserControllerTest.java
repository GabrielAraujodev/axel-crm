package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.config.ControllerTestConfig;
import com.axelcrm.auth.dto.UserRequest;
import com.axelcrm.auth.dto.UserResponse;
import com.axelcrm.commons.entity.enums.Role;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.auth.service.UserService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.axelcrm.auth.entity.User;
import com.axelcrm.auth.controller.UserController;

class UserControllerTest extends ControllerTestConfig {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        UserController controller = new UserController(userService);
        var settings = new SpringDataWebSettings(PageSerializationMode.VIA_DTO);
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new SpringDataJacksonConfiguration.PageModule(settings));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        var auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userId);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void findAll_ShouldReturn200() throws Exception {
        var user = new UserResponse(userId, "John", "john@test.com", Role.USER,
                true, ORG_ID, "Org", null, LocalDateTime.now());
        var page = new PageImpl<>(List.of(user));

        when(userService.findAll(eq(ORG_ID), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(userId.toString()));
    }

    @Test
    void findById_ShouldReturn200() throws Exception {
        var user = new UserResponse(userId, "John", "john@test.com", Role.USER,
                true, ORG_ID, "Org", null, LocalDateTime.now());

        when(userService.findById(ORG_ID, userId)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void findById_ShouldReturn404() throws Exception {
        when(userService.findById(ORG_ID, userId))
                .thenThrow(new ResourceNotFoundException("User", "id", userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldReturn200() throws Exception {
        var request = new UserRequest("Jane", "jane@test.com", "secret", Role.USER, true);
        var response = new UserResponse(UUID.randomUUID(), "Jane", "jane@test.com",
                Role.USER, true, ORG_ID, "Org", null, LocalDateTime.now());

        when(userService.create(eq(ORG_ID), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        var request = new UserRequest("Jane Updated", "jane@test.com", null, Role.ADMIN, true);
        var response = new UserResponse(userId, "Jane Updated", "jane@test.com",
                Role.ADMIN, true, ORG_ID, "Org", null, LocalDateTime.now());

        when(userService.update(eq(ORG_ID), eq(userId), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Jane Updated"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).delete(ORG_ID, userId);
    }
}
