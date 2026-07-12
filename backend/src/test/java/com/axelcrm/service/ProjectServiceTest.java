package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.dto.ProjectRequest;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Deal;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.entity.Project;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.ProjectRepository;
import com.axelcrm.auth.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
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
class ProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    ClientRepository clientRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    DealRepository dealRepository;

    @Mock
    com.axelcrm.repository.LegalProcessRepository legalProcessRepository;

    @InjectMocks
    ProjectService projectService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID projectId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Project createProject() {
        var org = new Organization();
        org.setId(orgId);

        var client = new Client();
        client.setId(clientId);
        client.setName("Acme Corp");

        var project = new Project();
        project.setId(projectId);
        project.setName("Website Redesign");
        project.setDescription("Complete website overhaul");
        project.setStatus("EM_ANDAMENTO");
        project.setBudget(new BigDecimal("50000"));
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 6, 30));
        project.setClient(client);
        project.setOrganization(org);
        return project;
    }

    @Test
    void findAll_ShouldReturnPagedProjects() {
        var project = createProject();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(project));

        when(projectRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<ProjectResponse> result = projectService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Website Redesign", result.getContent().getFirst().name());
    }

    @Test
    void findById_ShouldReturnProject() {
        var project = createProject();
        when(projectRepository.findByIdAndOrganization_Id(projectId, orgId))
                .thenReturn(Optional.of(project));

        ProjectResponse result = projectService.findById(orgId, projectId);

        assertNotNull(result);
        assertEquals(projectId, result.id());
        assertEquals("Complete website overhaul", result.description());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(projectRepository.findByIdAndOrganization_Id(projectId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.findById(orgId, projectId));
    }

    @Test
    void create_ShouldSaveAndReturnProject() {
        var request = new ProjectRequest(
                "New Project", "Description", LocalDate.of(2026, 2, 1), null,
                new BigDecimal("30000"), null, null, clientId, null);

        var saved = new Project();
        saved.setId(projectId);
        saved.setName("New Project");
        saved.setDescription("Description");
        saved.setBudget(new BigDecimal("30000"));

        var client = new Client();
        client.setId(clientId);

        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ProjectResponse result = projectService.create(orgId, request);

        assertNotNull(result);
        assertEquals("New Project", result.name());
    }

    @Test
    void create_ShouldThrowWhenClientNotFound() {
        var request = new ProjectRequest(
                "New Project", null, null, null, null, null, null, clientId, null);

        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.create(orgId, request));
    }

    @Test
    void update_ShouldModifyAndReturnProject() {
        var request = new ProjectRequest(
                "Updated", "New desc", null, null, new BigDecimal("60000"),
                new BigDecimal("45000"), "CONCLUIDO", clientId, null);
        var existing = createProject();

        var client = new Client();
        client.setId(clientId);

        when(projectRepository.findByIdAndOrganization_Id(projectId, orgId))
                .thenReturn(Optional.of(existing));
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        ProjectResponse result = projectService.update(orgId, projectId, request);

        assertNotNull(result);
        assertEquals("Updated", result.name());
        assertEquals("CONCLUIDO", result.status());
    }

    @Test
    void update_ShouldSetManagerWhenProvided() {
        var request = new ProjectRequest(
                "Updated", null, null, null, null, null, null, clientId, userId);
        var existing = createProject();

        var client = new Client();
        client.setId(clientId);
        var manager = new User();
        manager.setId(userId);

        when(projectRepository.findByIdAndOrganization_Id(projectId, orgId))
                .thenReturn(Optional.of(existing));
        when(clientRepository.findByIdAndOrganization_Id(clientId, orgId))
                .thenReturn(Optional.of(client));
        when(userRepository.findById(userId)).thenReturn(Optional.of(manager));
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        ProjectResponse result = projectService.update(orgId, projectId, request);

        assertEquals(userId, result.managerUserId());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var project = createProject();
        when(projectRepository.findByIdAndOrganization_Id(projectId, orgId))
                .thenReturn(Optional.of(project));

        projectService.delete(orgId, projectId);

        assertNotNull(project.getDeletedAt());
        verify(projectRepository).save(project);
    }

    /* ---- createFromDeal ---- */

    @Test
    void createFromDeal_ShouldConvertWonDealToProject() {
        var dealId = UUID.randomUUID();
        var client = new Client();
        client.setId(clientId);
        var manager = new User();
        manager.setId(userId);
        var deal = new Deal();
        deal.setId(dealId);
        deal.setTitle("Website Proposal");
        deal.setDescription("Full website rebuild");
        deal.setValue(new BigDecimal("50000"));
        deal.setWon(true);
        deal.setClient(client);
        deal.setAssignedTo(manager);

        var saved = new Project();
        saved.setId(projectId);
        saved.setName("Website Proposal");
        saved.setBudget(new BigDecimal("50000"));

        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.of(deal));
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ProjectResponse result = projectService.createFromDeal(orgId, dealId);

        assertNotNull(result);
        assertEquals("Website Proposal", result.name());
        assertEquals(new BigDecimal("50000"), result.budget());
    }

    @Test
    void createFromDeal_ShouldThrowWhenDealNotWon() {
        var dealId = UUID.randomUUID();
        var deal = new Deal();
        deal.setId(dealId);
        deal.setWon(false);

        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.of(deal));

        assertThrows(BadRequestException.class, () -> projectService.createFromDeal(orgId, dealId));
    }

    @Test
    void createFromDeal_ShouldThrowWhenDealNotFound() {
        var dealId = UUID.randomUUID();

        when(dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.createFromDeal(orgId, dealId));
    }
}
