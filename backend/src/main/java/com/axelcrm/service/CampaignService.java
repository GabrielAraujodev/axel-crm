package com.axelcrm.service;

import com.axelcrm.dto.CampaignRequest;
import com.axelcrm.dto.CampaignResponse;
import com.axelcrm.auth.entity.User;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.entity.Campaign;
import com.axelcrm.entity.CampaignRecipient;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contact;
import com.axelcrm.entity.Lead;
import com.axelcrm.repository.CampaignRecipientRepository;
import com.axelcrm.repository.CampaignRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContactRepository;
import com.axelcrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CampaignRecipientRepository campaignRecipientRepository;
    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final ContactRepository contactRepository;

    public List<CampaignResponse> findAll(UUID organizationId) {
        return campaignRepository.findByOrganization_Id(organizationId)
                .stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    public CampaignResponse findById(UUID organizationId, UUID id) {
        return campaignRepository.findByIdAndOrganization_Id(id, organizationId)
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));
    }

    @Transactional
    public CampaignResponse create(UUID organizationId, CampaignRequest request, UUID currentUserId) {
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Campaign campaign = new Campaign();
        campaign.setName(request.name());
        campaign.setType(request.type());
        campaign.setContent(request.content());
        campaign.setScheduledAt(request.scheduledAt());
        campaign.setStatus(request.status() != null ? request.status() : "RASCUNHO");
        campaign.setCreatedBy(creator);

        campaign = campaignRepository.save(campaign);
        return toResponse(campaign);
    }

    @Transactional
    public CampaignResponse update(UUID organizationId, UUID id, CampaignRequest request) {
        Campaign campaign = campaignRepository.findByIdAndOrganization_Id(id, organizationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));

        campaign.setName(request.name());
        campaign.setType(request.type());
        campaign.setContent(request.content());
        campaign.setScheduledAt(request.scheduledAt());
        if (request.status() != null) {
            campaign.setStatus(request.status());
            if ("ENVIADA".equalsIgnoreCase(request.status()) && campaign.getSentAt() == null) {
                campaign.setSentAt(java.time.LocalDateTime.now());
            }
        }

        campaign = campaignRepository.save(campaign);
        return toResponse(campaign);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Campaign campaign = campaignRepository.findByIdAndOrganization_Id(id, organizationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", id));
        campaign.setDeletedAt(java.time.LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    @Transactional
    public CampaignResponse sendCampaign(UUID organizationId, UUID campaignId) {
        Campaign campaign = campaignRepository.findByIdAndOrganization_Id(campaignId, organizationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign", "id", campaignId));

        if ("ENVIADA".equalsIgnoreCase(campaign.getStatus())) {
            throw new IllegalStateException("Campaign has already been sent");
        }

        // 1. Fetch target leads and clients to generate recipients list (simulation)
        List<Lead> leads = leadRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId);
        List<Client> clients = clientRepository.findAll().stream()
                .filter(c -> c.getOrganization().getId().equals(organizationId) && c.getDeletedAt() == null)
                .toList();

        int count = 0;
        // Generate recipients for Leads
        for (Lead lead : leads) {
            if (lead.getEmail() != null || lead.getPhone() != null) {
                CampaignRecipient recipient = new CampaignRecipient();
                recipient.setCampaign(campaign);
                recipient.setLead(lead);
                recipient.setEmail(lead.getEmail());
                recipient.setPhone(lead.getPhone());
                recipient.setStatus("SENT");
                recipient.setSentAt(java.time.LocalDateTime.now());
                campaignRecipientRepository.save(recipient);
                count++;
            }
        }

        // Generate recipients for Clients
        for (Client client : clients) {
            if (client.getEmail() != null || client.getPhone() != null) {
                CampaignRecipient recipient = new CampaignRecipient();
                recipient.setCampaign(campaign);
                recipient.setClient(client);
                recipient.setEmail(client.getEmail());
                recipient.setPhone(client.getPhone());
                recipient.setStatus("SENT");
                recipient.setSentAt(java.time.LocalDateTime.now());
                campaignRecipientRepository.save(recipient);
                count++;
            }
        }

        // 2. Update campaign statistics and status
        campaign.setStatus("ENVIADA");
        campaign.setSentAt(java.time.LocalDateTime.now());
        campaign.setRecipientsCount(count);
        campaign.setSentCount(count);
        // Simulate a few opens and clicks (e.g. 25% open, 5% click)
        campaign.setOpenCount((int) (count * 0.25));
        campaign.setClickCount((int) (count * 0.05));

        campaign = campaignRepository.save(campaign);
        return toResponse(campaign);
    }

    private CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getType(),
                campaign.getContent(),
                campaign.getScheduledAt(),
                campaign.getSentAt(),
                campaign.getRecipientsCount(),
                campaign.getSentCount(),
                campaign.getOpenCount(),
                campaign.getClickCount(),
                campaign.getStatus(),
                campaign.getCreatedBy() != null ? campaign.getCreatedBy().getId() : null,
                campaign.getCreatedAt(),
                campaign.getUpdatedAt()
        );
    }
}
