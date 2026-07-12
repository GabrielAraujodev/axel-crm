package com.axelcrm.service;

import com.axelcrm.dto.SupportTicketRequest;
import com.axelcrm.dto.SupportTicketResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.SupportTicket;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.SupportTicketRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public Page<SupportTicketResponse> findAll(UUID organizationId, Pageable pageable) {
        return supportTicketRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public SupportTicketResponse findById(UUID organizationId, UUID id) {
        return supportTicketRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
    }

    @Transactional
    public SupportTicketResponse create(UUID organizationId, SupportTicketRequest request, UUID currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        SupportTicket ticket = new SupportTicket();
        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());
        ticket.setStatus("ABERTO");
        ticket.setPriority("MEDIA");
        ticket.setCreatedBy(creator);

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            ticket.setClient(client);
        }

        if (request.assignedToId() != null) {
            User assigned = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToId()));
            ticket.setAssignedTo(assigned);
        }

        ticket = supportTicketRepository.save(ticket);
        return toResponse(ticket);
    }

    @Transactional
    public SupportTicketResponse update(UUID organizationId, UUID id, SupportTicketRequest request) {
        SupportTicket ticket = supportTicketRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));

        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            ticket.setClient(client);
        } else {
            ticket.setClient(null);
        }

        if (request.assignedToId() != null) {
            User assigned = userRepository.findById(request.assignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToId()));
            ticket.setAssignedTo(assigned);
        } else {
            ticket.setAssignedTo(null);
        }

        ticket = supportTicketRepository.save(ticket);
        return toResponse(ticket);
    }

    @Transactional
    public SupportTicketResponse changeStatus(UUID organizationId, UUID id, String status) {
        SupportTicket ticket = supportTicketRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));

        ticket.setStatus(status);
        if ("RESOLVIDO".equalsIgnoreCase(status) || "FECHADO".equalsIgnoreCase(status)) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else {
            ticket.setResolvedAt(null);
        }

        ticket = supportTicketRepository.save(ticket);
        return toResponse(ticket);
    }

    @Transactional
    public SupportTicketResponse changePriority(UUID organizationId, UUID id, String priority) {
        SupportTicket ticket = supportTicketRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));

        ticket.setPriority(priority);
        ticket = supportTicketRepository.save(ticket);
        return toResponse(ticket);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        SupportTicket ticket = supportTicketRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", id));
        ticket.setDeletedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
    }

    private SupportTicketResponse toResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getClient() != null ? ticket.getClient().getId() : null,
                ticket.getClient() != null ? ticket.getClient().getName() : null,
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null,
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : null,
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getId() : null,
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
