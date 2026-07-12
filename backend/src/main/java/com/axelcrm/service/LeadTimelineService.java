package com.axelcrm.service;

import com.axelcrm.dto.TimelineItemResponse;
import com.axelcrm.entity.AuditLog;
import com.axelcrm.entity.LeadNote;
import com.axelcrm.repository.AuditLogRepository;
import com.axelcrm.repository.LeadNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeadTimelineService {

    private final LeadNoteRepository leadNoteRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public List<TimelineItemResponse> getTimeline(UUID organizationId, UUID leadId) {
        List<TimelineItemResponse> items = new ArrayList<>();

        // 1. Fetch custom notes
        List<LeadNote> notes = leadNoteRepository
                .findByOrganization_IdAndLead_IdAndDeletedAtIsNullOrderByCreatedAtDesc(organizationId, leadId);
        for (LeadNote note : notes) {
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

        // 2. Fetch system audit logs for this lead
        List<AuditLog> auditLogs = auditLogRepository
                .findByOrganization_IdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        organizationId, "Lead", leadId.toString());
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
            return "Lead cadastrado no sistema.";
        } else if ("UPDATE".equals(action)) {
            return "Informações do Lead atualizadas.";
        } else if ("DELETE".equals(action)) {
            return "Lead removido/arquivado.";
        } else if ("CONVERT".equals(action)) {
            return "Lead convertido com sucesso em Cliente.";
        }
        return "Ação executada: " + log.getAction() + " no cadastro do Lead.";
    }
}
