package com.axelcrm.service;

import com.axelcrm.dto.ClientResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Lead;
import com.axelcrm.entity.enums.LeadStage;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import com.axelcrm.commons.entity.BaseEntity;

@Service
@RequiredArgsConstructor
public class LeadConversionService {

    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public ClientResponse convertLeadToClient(UUID organizationId, UUID leadId, UUID userId) {
        Lead lead = leadRepository.findByIdAndOrganization_Id(leadId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        Client client;
        if (lead.getStage() == LeadStage.CONVERTED && lead.getConvertedClient() != null) {
            client = lead.getConvertedClient();
        } else {
            // 1. Create a new Client from Lead details
            client = new Client();
            client.setName(lead.getName());
            client.setEmail(lead.getEmail());
            client.setPhone(lead.getPhone());
            client.setCompanyName(lead.getCompany());
            client.setNotes(lead.getNotes());
            client.setAssignedTo(lead.getAssignedTo());
            client.setActive(true);
            
            // Save Client (BaseEntity prePersist hook handles organizationId)
            client = clientRepository.save(client);

            // 2. Link Lead to the Client
            lead.setConvertedClient(client);
            lead.setConvertedAt(LocalDateTime.now());
            lead.setStage(LeadStage.CONVERTED);
            leadRepository.save(lead);

            // 3. Log Audit Events
            auditLogService.log(
                    organizationId, 
                    userId, 
                    "CONVERT", 
                    "Lead", 
                    leadId.toString(), 
                    "Stage: " + LeadStage.NEW, 
                    "Stage: CONVERTED, Client ID: " + client.getId()
            );
            
            auditLogService.log(
                    organizationId, 
                    userId, 
                    "CREATE", 
                    "Client", 
                    client.getId().toString(), 
                    null, 
                    "Criado por conversão do Lead: " + lead.getName()
            );
        }

        return toClientResponse(client);
    }

    private ClientResponse toClientResponse(Client client) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getDocument(),
                client.getCompanyName(),
                client.getWebsite(),
                client.getIndustry(),
                client.getAddress(),
                client.getCity(),
                client.getState(),
                client.getZipCode(),
                client.getCountry(),
                client.getNotes(),
                client.isActive(),
                client.getStatus(),
                client.getServiceType(),
                client.getAssignedTo() != null ? client.getAssignedTo().getId() : null,
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}
