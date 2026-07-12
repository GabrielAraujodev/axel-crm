package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.ProposalRequest;
import com.axelcrm.dto.ProposalResponse;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Proposal;
import com.axelcrm.entity.enums.ProposalStatus;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ProposalItemRepository;
import com.axelcrm.repository.ProposalRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
class ProposalServiceTest {

    @Mock
    ProposalRepository proposalRepository;

    @Mock
    ProposalItemRepository proposalItemRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ProposalService proposalService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID proposalId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();

    private Proposal createProposal() {
        var org = new Organization();
        org.setId(orgId);

        var client = new Client();
        client.setId(clientId);
        client.setName("ClientCo");

        var proposal = new Proposal();
        proposal.setId(proposalId);
        proposal.setTitle("Website Proposal");
        proposal.setDescription("Complete website design and development");
        proposal.setStatus(ProposalStatus.DRAFT);
        proposal.setTotalAmount(new BigDecimal("15000"));
        proposal.setDiscountAmount(BigDecimal.ZERO);
        proposal.setIssueDate(LocalDate.of(2026, 7, 1));
        proposal.setValidUntil(LocalDate.of(2026, 8, 1));
        proposal.setClient(client);
        proposal.setOrganization(org);
        return proposal;
    }

    @Test
    void findAll_ShouldReturnPagedProposals() {
        var proposal = createProposal();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(proposal));

        when(proposalRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<ProposalResponse> result = proposalService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Website Proposal", result.getContent().getFirst().title());
    }

    @Test
    void findById_ShouldReturnProposal() {
        var proposal = createProposal();
        when(proposalRepository.findByIdAndOrganization_Id(proposalId, orgId))
                .thenReturn(Optional.of(proposal));

        ProposalResponse result = proposalService.findById(orgId, proposalId);

        assertNotNull(result);
        assertEquals(proposalId, result.id());
        assertEquals("Complete website design and development", result.description());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(proposalRepository.findByIdAndOrganization_Id(proposalId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> proposalService.findById(orgId, proposalId));
    }

    @Test
    void create_ShouldSaveAndReturnProposal() {
        var request = new ProposalRequest(
                "New Proposal", "Description", ProposalStatus.DRAFT,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 1),
                BigDecimal.ZERO, clientId, null, null, null);

        var client = new Client();
        client.setId(clientId);

        var saved = new Proposal();
        saved.setId(proposalId);
        saved.setTitle("New Proposal");
        saved.setDescription("Description");
        saved.setTotalAmount(BigDecimal.ZERO);
        saved.setClient(client);

        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(proposalRepository.save(any(Proposal.class))).thenReturn(saved);

        ProposalResponse result = proposalService.create(orgId, request);

        assertNotNull(result);
        assertEquals("New Proposal", result.title());
    }

    @Test
    void create_ShouldThrowWhenClientNotFound() {
        ProposalRequest request = new ProposalRequest("New Proposal", null, null, null, null, null, clientId, null, null, null);

        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> proposalService.create(orgId, request));
    }

    @Test
    void update_ShouldModifyAndReturnProposal() {
        ProposalRequest request = new ProposalRequest("Updated Proposal", "Updated desc", ProposalStatus.SENT, LocalDate.now(), LocalDate.now().plusDays(10), BigDecimal.valueOf(100), clientId, null, null, null);
        var existing = createProposal();

        var client = new Client();
        client.setId(clientId);

        when(proposalRepository.findByIdAndOrganization_Id(proposalId, orgId))
                .thenReturn(Optional.of(existing));
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(i -> i.getArgument(0));

        ProposalResponse result = proposalService.update(orgId, proposalId, request);

        assertNotNull(result);
        assertEquals("Updated Proposal", result.title());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var proposal = createProposal();
        when(proposalRepository.findByIdAndOrganization_Id(proposalId, orgId))
                .thenReturn(Optional.of(proposal));

        proposalService.delete(orgId, proposalId);

        assertNotNull(proposal.getDeletedAt());
        verify(proposalRepository).save(proposal);
    }
}
