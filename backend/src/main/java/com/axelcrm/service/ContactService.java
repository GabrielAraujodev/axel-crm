package com.axelcrm.service;

import com.axelcrm.dto.ContactRequest;
import com.axelcrm.dto.ContactResponse;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contact;
import com.axelcrm.entity.enums.ContactType;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ClientRepository clientRepository;

    public Page<ContactResponse> findAll(UUID organizationId, Pageable pageable) {
        return contactRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public ContactResponse findById(UUID organizationId, UUID id) {
        return contactRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
    }

    public List<ContactResponse> findByClientId(UUID organizationId, UUID clientId) {
        return contactRepository.findByClient_IdAndClient_Organization_Id(clientId, organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ContactResponse create(UUID organizationId, ContactRequest request) {
        Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        Contact contact = new Contact();
        contact.setName(request.firstName() + (request.lastName() != null ? " " + request.lastName() : ""));
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setRole(request.jobTitle());
        contact.setNotes(request.notes());
        contact.setContactType(request.contactType() != null ? request.contactType() : ContactType.OTHER);
        contact.setClient(client);

        contact = contactRepository.save(contact);
        return toResponse(contact);
    }

    @Transactional
    public ContactResponse update(UUID organizationId, UUID id, ContactRequest request) {
        Contact contact = contactRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));

        Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        contact.setName(request.firstName() + (request.lastName() != null ? " " + request.lastName() : ""));
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setRole(request.jobTitle());
        contact.setNotes(request.notes());
        contact.setContactType(request.contactType() != null ? request.contactType() : ContactType.OTHER);
        contact.setClient(client);

        contact = contactRepository.save(contact);
        return toResponse(contact);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Contact contact = contactRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
        contact.setDeletedAt(java.time.LocalDateTime.now());
        contactRepository.save(contact);
    }

    private ContactResponse toResponse(Contact contact) {
        String fullName = contact.getName();
        String firstName = "";
        String lastName = "";
        if (fullName != null) {
            int spaceIndex = fullName.indexOf(' ');
            if (spaceIndex != -1) {
                firstName = fullName.substring(0, spaceIndex);
                lastName = fullName.substring(spaceIndex + 1);
            } else {
                firstName = fullName;
            }
        }

        ClientResponse clientResponse = null;
        if (contact.getClient() != null) {
            Client client = contact.getClient();
            clientResponse = new ClientResponse(
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
}
