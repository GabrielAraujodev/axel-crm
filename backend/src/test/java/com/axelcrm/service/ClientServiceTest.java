package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.ClientRequest;
import com.axelcrm.dto.ClientResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.auth.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    ClientRepository clientRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ClientService clientService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Client createClient() {
        var org = new Organization();
        org.setId(orgId);

        var client = new Client();
        client.setId(clientId);
        client.setName("Acme Corp");
        client.setEmail("contact@acme.com");
        client.setActive(true);
        client.setOrganization(org);
        return client;
    }

    @Test
    void findAll_ShouldReturnPagedClients() {
        var client = createClient();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(client));

        when(clientRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<ClientResponse> result = clientService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Acme Corp", result.getContent().getFirst().name());
    }

    @Test
    void findById_ShouldReturnClient() {
        var client = createClient();
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));

        ClientResponse result = clientService.findById(orgId, clientId);

        assertNotNull(result);
        assertEquals(clientId, result.id());
        assertEquals("contact@acme.com", result.email());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(orgId, clientId));
    }

    @Test
    void create_ShouldSaveAndReturnClient() {
        var request = new ClientRequest(
                "Client 1", "client1@test.com", "11999999999", "12345678901",
                "Company 1", "www.client1.com", "Address 1", "City 1",
                "State 1", "12345-678", "Country 1", "Tech",
                "Notes 1", true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null);

        var saved = new Client();
        saved.setId(clientId);
        saved.setName("NewCo");
        saved.setEmail("info@newco.com");
        saved.setActive(true);

        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        ClientResponse result = clientService.create(orgId, request);

        assertNotNull(result);
        assertEquals("NewCo", result.name());
        assertEquals("info@newco.com", result.email());
    }

    @Test
    void create_ShouldSetAssignedToWhenProvided() {
        var request = new ClientRequest(
                "NewCo", null, null, null, null, null, null,
                null, null, null, null, null, null, true, null, userId);

        var assigned = new User();
        assigned.setId(userId);
        assigned.setName("Agent");

        var saved = new Client();
        saved.setId(clientId);
        saved.setName("NewCo");
        saved.setActive(true);
        saved.setAssignedTo(assigned);

        when(userRepository.findById(userId)).thenReturn(Optional.of(assigned));
        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        ClientResponse result = clientService.create(orgId, request);

        assertNotNull(result);
        assertEquals(userId, result.assignedToUserId());
    }

    @Test
    void update_ShouldModifyAndReturnClient() {
        var request = new ClientRequest(
                "Updated Client", null, null, null, null, null, null, null,
                null, null, null, null, null, true, com.axelcrm.entity.enums.ClientStatus.ACTIVE, null);
        var existing = createClient();

        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(existing));
        when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

        ClientResponse result = clientService.update(orgId, clientId, request);

        assertNotNull(result);
        assertEquals("Updated Client", result.name());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var client = createClient();
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));

        clientService.delete(orgId, clientId);

        assertNotNull(client.getDeletedAt());
        verify(clientRepository).save(client);
    }
}
