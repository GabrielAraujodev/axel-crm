package com.axelcrm.service;

import com.axelcrm.dto.ClientRequest;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public Page<ClientResponse> findAll(UUID organizationId, Pageable pageable) {
        return clientRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public ClientResponse findById(UUID organizationId, UUID id) {
        return clientRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }

    @Transactional
    public ClientResponse create(UUID organizationId, ClientRequest request) {
        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setDocument(request.taxId());
        client.setCompanyName(request.companyName());
        client.setWebsite(request.website());
        client.setIndustry(request.industry());
        client.setAddress(request.address());
        client.setCity(request.city());
        client.setState(request.state());
        client.setZipCode(request.zipCode());
        client.setCountry(request.country());
        client.setNotes(request.notes());
        client.setServiceType(request.serviceType());
        client.setActive(request.active() != null ? request.active() : true);
        if (request.status() != null) {
            client.setStatus(request.status());
        }

        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            client.setAssignedTo(assigned);
        }

        client = clientRepository.save(client);
        return toResponse(client);
    }

    @Transactional
    public ClientResponse update(UUID organizationId, UUID id, ClientRequest request) {
        Client client = clientRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPhone(request.phone());
        client.setDocument(request.taxId());
        client.setCompanyName(request.companyName());
        client.setWebsite(request.website());
        client.setIndustry(request.industry());
        client.setAddress(request.address());
        client.setCity(request.city());
        client.setState(request.state());
        client.setZipCode(request.zipCode());
        client.setCountry(request.country());
        client.setNotes(request.notes());
        client.setServiceType(request.serviceType());
        if (request.active() != null) {
            client.setActive(request.active());
        }
        if (request.status() != null) {
            client.setStatus(request.status());
        }

        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            client.setAssignedTo(assigned);
        } else {
            client.setAssignedTo(null);
        }

        client = clientRepository.save(client);
        return toResponse(client);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Client client = clientRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        client.setDeletedAt(java.time.LocalDateTime.now());
        clientRepository.save(client);
    }

    private ClientResponse toResponse(Client client) {
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
