package com.axelcrm.controller;

import com.axelcrm.dto.IntegrationRequest;
import com.axelcrm.dto.IntegrationResponse;
import com.axelcrm.auth.security.TenantContext;
import com.axelcrm.service.IntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integrations")
@RequiredArgsConstructor
@Tag(name = "Integrations", description = "Endpoints for managing integrations with external tools (like Whatsapp, email providers)")
public class IntegrationController {

    private final IntegrationService integrationService;

    @GetMapping
    @Operation(summary = "List all integrations")
    public ResponseEntity<Page<IntegrationResponse>> findAll(Pageable pageable) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(integrationService.findAll(organizationId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an integration by ID")
    public ResponseEntity<IntegrationResponse> findById(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(integrationService.findById(organizationId, id));
    }

    @PostMapping
    @Operation(summary = "Create a new integration")
    public ResponseEntity<IntegrationResponse> create(@Valid @RequestBody IntegrationRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(integrationService.create(organizationId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing integration")
    public ResponseEntity<IntegrationResponse> update(@PathVariable UUID id, @Valid @RequestBody IntegrationRequest request) {
        UUID organizationId = TenantContext.getOrganizationId();
        return ResponseEntity.ok(integrationService.update(organizationId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete an integration")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID organizationId = TenantContext.getOrganizationId();
        integrationService.delete(organizationId, id);
        return ResponseEntity.noContent().build();
    }

    private static boolean googleConnected = false;

    @GetMapping("/google/status")
    @Operation(summary = "Get Google OAuth2 connection status")
    public ResponseEntity<java.util.Map<String, Object>> getGoogleStatus() {
        return ResponseEntity.ok(java.util.Map.of(
                "connected", googleConnected,
                "email", googleConnected ? "contato@axelpro.com.br" : ""
        ));
    }

    @PostMapping("/google/connect")
    @Operation(summary = "Connect to Google OAuth2 (Mock)")
    public ResponseEntity<java.util.Map<String, Object>> connectGoogle() {
        googleConnected = true;
        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Google Account connected successfully."));
    }

    @PostMapping("/google/disconnect")
    @Operation(summary = "Disconnect Google OAuth2")
    public ResponseEntity<java.util.Map<String, Object>> disconnectGoogle() {
        googleConnected = false;
        return ResponseEntity.ok(java.util.Map.of("success", true));
    }

    @GetMapping("/google/calendar")
    @Operation(summary = "Get synced Google Calendar events (Mock)")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getCalendarEvents() {
        return ResponseEntity.ok(java.util.List.of(
                java.util.Map.of("title", "Audiência de Perícia - Proc. 1002302-12", "time", "Amanhã às 14:00", "location", "Fórum Central"),
                java.util.Map.of("title", "Reunião de Alinhamento com Cliente", "time", "Quarta-feira às 10:00", "location", "Google Meet")
        ));
    }

    @PostMapping("/ai/generate")
    @Operation(summary = "Generate professional text draft using Google Gemini AI (Mocked)")
    public ResponseEntity<java.util.Map<String, String>> generateAiText(@RequestBody java.util.Map<String, String> request) {
        String prompt = request.getOrDefault("prompt", "");
        String context = request.getOrDefault("context", "");

        String text = "--- PARECER TÉCNICO DE PERÍCIA (GERADO POR IA GEMINI) ---\n\n" +
                "De acordo com as diretrizes indicadas (" + prompt + "):\n\n" +
                "1. ANÁLISE INICIAL: Com base no contexto fornecido (" + context + "), identificou-se a conformidade dos parâmetros técnicos e processuais.\n\n" +
                "2. CONCLUSÃO: Resta demonstrado o nexo causal, sugerindo a aprovação do laudo de perícia judicial com as devidas ressalvas técnicas.\n\n" +
                "Rascunho gerado em conformidade com as normas regulamentadoras aplicáveis.";

        return ResponseEntity.ok(java.util.Map.of("text", text));
    }
}
