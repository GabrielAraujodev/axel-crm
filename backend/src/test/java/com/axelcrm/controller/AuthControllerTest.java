package com.axelcrm.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.axelcrm.auth.dto.AuthRequest;
import com.axelcrm.auth.dto.LoginResponse;
import com.axelcrm.auth.dto.RegisterRequest;
import com.axelcrm.commons.exception.GlobalExceptionHandler;
import com.axelcrm.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.axelcrm.auth.controller.AuthController;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void register_ShouldReturn200AndToken() throws Exception {
        var request = new RegisterRequest("Org", "John", "john@test.com", "pass123");
        var response = new LoginResponse("jwt", UUID.randomUUID(), "John",
                "john@test.com", "ADMIN", UUID.randomUUID(), "Org");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.userName").value("John"));
    }

    @Test
    void login_ShouldReturn200AndToken() throws Exception {
        var request = new AuthRequest("john@test.com", "pass123");
        var response = new LoginResponse("jwt", UUID.randomUUID(), "John",
                "john@test.com", "ADMIN", UUID.randomUUID(), "Org");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void login_ShouldReturn401WhenBadCredentials() throws Exception {
        var request = new AuthRequest("john@test.com", "wrong");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
