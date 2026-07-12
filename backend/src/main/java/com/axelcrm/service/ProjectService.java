package com.axelcrm.service;

import com.axelcrm.dto.ProjectRequest;
import com.axelcrm.dto.ProjectResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.Project;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.BadRequestException;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.ProjectRepository;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.repository.LegalProcessRepository;
import com.axelcrm.entity.LegalProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final DealRepository dealRepository;
    private final LegalProcessRepository legalProcessRepository;

    public Page<ProjectResponse> findAll(UUID organizationId, Pageable pageable) {
        return projectRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public Page<ProjectResponse> findByClient(UUID organizationId, UUID clientId, Pageable pageable) {
        return projectRepository.findByClient_IdAndClient_Organization_Id(clientId, organizationId, pageable)
                .map(this::toResponse);
    }

    public ProjectResponse findById(UUID organizationId, UUID id) {
        return projectRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    @Transactional
    public ProjectResponse create(UUID organizationId, ProjectRequest request) {
        Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setBudget(request.budget());
        project.setCost(request.cost());
        project.setStatus(request.status() != null ? request.status() : "PLANEJAMENTO");
        project.setClient(client);

        if (request.managerUserId() != null) {
            User manager = userRepository.findById(request.managerUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.managerUserId()));
            project.setManager(manager);
        }

        if (request.legalProcessId() != null) {
            LegalProcess process = legalProcessRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.legalProcessId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("LegalProcess", "id", request.legalProcessId()));
            project.setLegalProcess(process);
        }
        project.setCnjNumber(request.cnjNumber());
        project.setExpertType(request.expertType());
        project.setPaymentStatus(request.paymentStatus());
        project.setDeliveryDeadline(request.deliveryDeadline());

        project = projectRepository.save(project);
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse update(UUID organizationId, UUID id, ProjectRequest request) {
        Project project = projectRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));

        project.setName(request.name());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setBudget(request.budget());
        project.setCost(request.cost());
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        project.setClient(client);

        if (request.managerUserId() != null) {
            User manager = userRepository.findById(request.managerUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.managerUserId()));
            project.setManager(manager);
        } else {
            project.setManager(null);
        }

        if (request.legalProcessId() != null) {
            LegalProcess process = legalProcessRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.legalProcessId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("LegalProcess", "id", request.legalProcessId()));
            project.setLegalProcess(process);
        } else {
            project.setLegalProcess(null);
        }
        project.setCnjNumber(request.cnjNumber());
        project.setExpertType(request.expertType());
        project.setPaymentStatus(request.paymentStatus());
        project.setDeliveryDeadline(request.deliveryDeadline());

        project = projectRepository.save(project);
        return toResponse(project);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Project project = projectRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
        project.setDeletedAt(java.time.LocalDateTime.now());
        projectRepository.save(project);
    }

    @Transactional
    public ProjectResponse createFromDeal(UUID organizationId, UUID dealId) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(dealId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", dealId));

        if (deal.getWon() == null || !deal.getWon()) {
            throw new BadRequestException("Deal must be won before converting to a project");
        }

        Project project = new Project();
        project.setName(deal.getTitle());
        project.setDescription(deal.getDescription());
        project.setBudget(deal.getValue());
        project.setClient(deal.getClient());
        project.setManager(deal.getAssignedTo());
        project.setStatus("PLANEJAMENTO");

        project = projectRepository.save(project);
        return toResponse(project);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getBudget(),
                project.getCost(),
                project.getStatus(),
                project.getClient() != null ? project.getClient().getId() : null,
                project.getClient() != null ? project.getClient().getName() : null,
                project.getManager() != null ? project.getManager().getId() : null,
                project.getManager() != null ? project.getManager().getName() : null,
                project.getLegalProcess() != null ? project.getLegalProcess().getId() : null,
                project.getLegalProcess() != null ? project.getLegalProcess().getCnjNumber() : null,
                project.getCnjNumber(),
                project.getExpertType(),
                project.getPaymentStatus(),
                project.getDeliveryDeadline(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
