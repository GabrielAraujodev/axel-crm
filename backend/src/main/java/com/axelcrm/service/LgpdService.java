package com.axelcrm.service;

import com.axelcrm.auth.dto.UserResponse;
import com.axelcrm.dto.ConsentRequest;
import com.axelcrm.dto.ConsentResponse;
import com.axelcrm.dto.ContactResponse;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.dto.LeadResponse;
import com.axelcrm.dto.LgpdDataExportResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Consent;
import com.axelcrm.entity.Contact;
import com.axelcrm.entity.Lead;
import com.axelcrm.entity.enums.ContactType;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ConsentRepository;
import com.axelcrm.repository.ContactRepository;
import com.axelcrm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service managing LGPD compliance including consent registry, data portability export,
 * and data deletion / anonymization.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class LgpdService {

    private final ConsentRepository consentRepository;
    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final ContactRepository contactRepository;

    @Transactional
    public ConsentResponse saveConsent(UUID organizationId, ConsentRequest request) {
        // Find existing consent for this email and type in the organization
        List<Consent> existingList = consentRepository
                .findByPersonEmailAndOrganization_IdAndDeletedAtIsNull(request.personEmail(), organizationId);

        Consent consent = existingList.stream()
                .filter(c -> c.getConsentType().equalsIgnoreCase(request.consentType()))
                .findFirst()
                .orElse(new Consent());

        consent.setPersonEmail(request.personEmail());
        consent.setConsentType(request.consentType());
        consent.setGranted(request.granted());
        consent.setIpAddress(request.ipAddress());
        consent.setUserAgent(request.userAgent());
        consent.setConsentedAt(LocalDateTime.now());

        consent = consentRepository.save(consent);
        return toConsentResponse(consent);
    }

    @Transactional(readOnly = true)
    public LgpdDataExportResponse exportData(UUID organizationId, String email) {
        List<LeadResponse> leads = leadRepository.findByEmailAndOrganization_Id(email, organizationId)
                .stream()
                .filter(l -> l.getDeletedAt() == null)
                .map(this::toLeadResponse)
                .toList();

        List<ClientResponse> clients = clientRepository.findByEmailAndOrganization_Id(email, organizationId)
                .stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(this::toClientResponse)
                .toList();

        List<ContactResponse> contacts = contactRepository.findByEmailAndOrganization_Id(email, organizationId)
                .stream()
                .filter(co -> co.getDeletedAt() == null)
                .map(this::toContactResponse)
                .toList();

        List<ConsentResponse> consents = consentRepository.findByPersonEmailAndOrganization_IdAndDeletedAtIsNull(email, organizationId)
                .stream()
                .map(this::toConsentResponse)
                .toList();

        return new LgpdDataExportResponse(email, leads, clients, contacts, consents);
    }

    @Transactional
    public void deleteData(UUID organizationId, String email) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Leads — irreversibly anonymize all PII
        List<Lead> leads = leadRepository.findByEmailAndOrganization_Id(email, organizationId);
        for (Lead lead : leads) {
            lead.setDeletedAt(now);
            lead.setName(null);
            lead.setPhone(null);
            lead.setEmail(null);
            lead.setCompany(null);
            lead.setPosition(null);
            lead.setNotes(null);
            leadRepository.save(lead);
        }

        // 2. Clients — irreversibly anonymize all PII
        List<Client> clients = clientRepository.findByEmailAndOrganization_Id(email, organizationId);
        for (Client client : clients) {
            client.setDeletedAt(now);
            client.setName(null);
            client.setPhone(null);
            client.setEmail(null);
            client.setDocument(null);
            client.setCompanyName(null);
            client.setAddress(null);
            client.setCity(null);
            client.setState(null);
            client.setZipCode(null);
            client.setNotes(null);
            clientRepository.save(client);
        }

        // 3. Contacts — irreversibly anonymize all PII
        List<Contact> contacts = contactRepository.findByEmailAndOrganization_Id(email, organizationId);
        for (Contact contact : contacts) {
            contact.setDeletedAt(now);
            contact.setName(null);
            contact.setPhone(null);
            contact.setEmail(null);
            contact.setNotes(null);
            contactRepository.save(contact);
        }

        // 4. Consents — hard DELETE (Art. 18, VI LGPD: elimination upon request)
        List<Consent> consents = consentRepository.findByPersonEmailAndOrganization_IdAndDeletedAtIsNull(email, organizationId);
        for (Consent consent : consents) {
            consentRepository.delete(consent);
        }
    }

    private LeadResponse toLeadResponse(Lead lead) {
        UserResponse assignedTo = lead.getAssignedTo() != null
                ? new UserResponse(lead.getAssignedTo().getId(), lead.getAssignedTo().getName(),
                lead.getAssignedTo().getEmail(), lead.getAssignedTo().getRole(),
                lead.getAssignedTo().isActive(), null, null, null, null)
                : null;

        return new LeadResponse(
                lead.getId(), lead.getName(), lead.getEmail(), lead.getPhone(),
                lead.getCompany(), lead.getPosition(), lead.getSource(), lead.getStage(),
                lead.getEstimatedValue(), lead.getNotes(), assignedTo,
                lead.getCreatedAt(), lead.getUpdatedAt()
        );
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

    private ContactResponse toContactResponse(Contact contact) {
        ClientResponse clientResponse = contact.getClient() != null
                ? toClientResponse(contact.getClient())
                : null;

        String name = contact.getName() != null ? contact.getName() : "";
        String[] parts = name.split(" ", 2);
        String firstName = parts.length > 0 ? parts[0] : "";
        String lastName = parts.length > 1 ? parts[1] : "";

        return new ContactResponse(
                contact.getId(),
                firstName,
                lastName,
                contact.getEmail(),
                contact.getPhone(),
                contact.getRole(),
                null,
                contact.getNotes(),
                contact.getContactType(),
                clientResponse,
                null,
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }

    private ConsentResponse toConsentResponse(Consent consent) {
        return new ConsentResponse(
                consent.getId(),
                consent.getPersonEmail(),
                consent.getConsentType(),
                consent.isGranted(),
                consent.getIpAddress(),
                consent.getUserAgent(),
                consent.getConsentedAt(),
                consent.getCreatedAt(),
                consent.getUpdatedAt()
        );
    }
}
