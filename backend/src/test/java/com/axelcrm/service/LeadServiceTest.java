package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.LeadRequest;
import com.axelcrm.dto.LeadResponse;
import com.axelcrm.entity.Lead;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.auth.entity.User;
import com.axelcrm.entity.enums.LeadSource;
import com.axelcrm.entity.enums.LeadStage;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.LeadRepository;
import com.axelcrm.auth.repository.UserRepository;
import java.math.BigDecimal;
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
class LeadServiceTest {

    @Mock
    LeadRepository leadRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    LeadService leadService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID leadId = UUID.randomUUID();

    private Lead createLead() {
        var org = new Organization();
        org.setId(orgId);

        var lead = new Lead();
        lead.setId(leadId);
        lead.setName("Prospect");
        lead.setEmail("prospect@test.com");
        lead.setSource(LeadSource.WEBSITE);
        lead.setStage(LeadStage.NEW);
        lead.setEstimatedValue(BigDecimal.valueOf(10000));
        lead.setOrganization(org);
        return lead;
    }

    @Test
    void findAll_ShouldReturnPagedLeads() {
        var lead = createLead();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(lead));

        when(leadRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<LeadResponse> result = leadService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Prospect", result.getContent().getFirst().name());
    }

    @Test
    void findById_ShouldReturnLead() {
        var lead = createLead();
        when(leadRepository.findByIdAndOrganization_Id(leadId, orgId))
                .thenReturn(Optional.of(lead));

        LeadResponse result = leadService.findById(orgId, leadId);

        assertNotNull(result);
        assertEquals(leadId, result.id());
        assertEquals("Prospect", result.name());
        assertEquals(LeadStage.NEW, result.stage());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(leadRepository.findByIdAndOrganization_Id(leadId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> leadService.findById(orgId, leadId));
    }

    @Test
    void create_ShouldSaveAndReturnLead() {
        var request = new LeadRequest("Jane Doe", "jane@example.com", "987654321", "Acme", "CTO", LeadSource.WEBSITE, LeadStage.CONTACTED, "Some notes", null, BigDecimal.valueOf(2000), null, null);

        var saved = new Lead();
        saved.setId(leadId);
        saved.setName("New Lead");
        saved.setEmail("lead@test.com");
        saved.setSource(LeadSource.REFERRAL);
        saved.setStage(LeadStage.CONTACTED);

        when(leadRepository.save(any(Lead.class))).thenReturn(saved);

        LeadResponse result = leadService.create(orgId, request);

        assertNotNull(result);
        assertEquals("New Lead", result.name());
        assertEquals(LeadSource.REFERRAL, result.source());
    }

    @Test
    void create_ShouldSetAssignedToWhenProvided() {
        var assignedId = UUID.randomUUID();
        LeadRequest request = new LeadRequest("Invalid Lead", null, null, null, null, LeadSource.OTHER, LeadStage.NEW, null, null, null, assignedId, null);

        var assigned = new User();
        assigned.setId(assignedId);
        assigned.setName("Seller");

        var saved = new Lead();
        saved.setId(leadId);
        saved.setName("Lead");
        saved.setStage(LeadStage.NEW);
        saved.setSource(LeadSource.OTHER);
        saved.setAssignedTo(assigned);

        when(userRepository.findById(assignedId)).thenReturn(Optional.of(assigned));
        when(leadRepository.save(any(Lead.class))).thenReturn(saved);

        LeadResponse result = leadService.create(orgId, request);

        assertNotNull(result);
        assertNotNull(result.assignedTo());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var lead = createLead();
        when(leadRepository.findByIdAndOrganization_Id(leadId, orgId))
                .thenReturn(Optional.of(lead));

        leadService.delete(orgId, leadId);

        assertNotNull(lead.getDeletedAt());
        verify(leadRepository).save(lead);
    }
}
