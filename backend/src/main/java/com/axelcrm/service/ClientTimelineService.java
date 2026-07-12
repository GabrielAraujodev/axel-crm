package com.axelcrm.service;

import com.axelcrm.dto.TimelineItemResponse;
import com.axelcrm.entity.AuditLog;
import com.axelcrm.entity.ClientNote;
import com.axelcrm.repository.AuditLogRepository;
import com.axelcrm.repository.ClientNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientTimelineService {

    private final ClientNoteRepository clientNoteRepository;
    private final AuditLogRepository auditLogRepository;

    public List<TimelineItemResponse> getTimeline(UUID organizationId, UUID clientId) {
        List<TimelineItemResponse> items = new ArrayList<>();

        // 1. Fetch custom notes
        List<ClientNote> notes = clientNoteRepository
                .findByOrganization_IdAndClient_IdAndDeletedAtIsNullOrderByCreatedAtDesc(organizationId, clientId);
        for (ClientNote note : notes) {
            items.add(new TimelineItemResponse(
                    note.getId(),
                    "NOTE",
                    "ADD_NOTE",
                    note.getContent(),
                    note.getUser() != null ? note.getUser().getId() : null,
                    note.getUser() != null ? note.getUser().getName() : "Sistema",
                    note.getCreatedAt()
            ));
        }

        // 2. Fetch system audit logs for this client
        List<AuditLog> auditLogs = auditLogRepository
                .findByOrganization_IdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(organizationId, "Client", clientId.toString());
        for (AuditLog log : auditLogs) {
            String friendlyContent = formatAuditLog(log);
            items.add(new TimelineItemResponse(
                    log.getId(),
                    "SYSTEM_LOG",
                    log.getAction(),
                    friendlyContent,
                    log.getUser() != null ? log.getUser().getId() : null,
                    log.getUser() != null ? log.getUser().getName() : "Sistema",
                    log.getCreatedAt()
            ));
        }

        // 3. Sort chronologically (descending)
        items.sort(Comparator.comparing(TimelineItemResponse::createdAt).reversed());
        return items;
    }

    private String formatAuditLog(AuditLog log) {
        String action = log.getAction().toUpperCase();
        if ("CREATE".equals(action)) {
            return "Cadastro inicial do cliente criado no sistema.";
        } else if ("UPDATE".equals(action)) {
            return "Informações cadastrais do cliente atualizadas.";
        } else if ("DELETE".equals(action)) {
            return "Cliente removido/arquivado do sistema.";
        }
        return "Ação executada: " + log.getAction() + " no cadastro do cliente.";
    }
}
