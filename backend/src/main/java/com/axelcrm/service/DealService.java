package com.axelcrm.service;

import com.axelcrm.dto.DealRequest;
import com.axelcrm.dto.DealResponse;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.Pipeline;
import com.axelcrm.entity.PipelineStage;
import com.axelcrm.auth.entity.User;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Contact;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.PipelineRepository;
import com.axelcrm.repository.PipelineStageRepository;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DealService {

    private final DealRepository dealRepository;
    private final PipelineRepository pipelineRepository;
    private final PipelineStageRepository pipelineStageRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ContactRepository contactRepository;

    public Page<DealResponse> findAll(UUID organizationId, Pageable pageable) {
        return dealRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public DealResponse findById(UUID organizationId, UUID id) {
        return dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
    }

    @Transactional
    public DealResponse create(UUID organizationId, DealRequest request) {
        Deal deal = new Deal();
        deal.setTitle(request.title());
        deal.setDescription(request.description());
        deal.setValue(request.value());

        if (request.pipelineId() != null) {
            Pipeline pipeline = pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.pipelineId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", request.pipelineId()));
            deal.setPipeline(pipeline);
        }
        if (request.stageId() != null) {
            PipelineStage stage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.stageId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Stage", "id", request.stageId()));
            deal.setStage(stage);
        }
        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            deal.setClient(client);
        }
        if (request.contactId() != null) {
            Contact contact = contactRepository.findByIdAndOrganization_Id(request.contactId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.contactId()));
            deal.setContact(contact);
        }
        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            deal.setAssignedTo(assigned);
        }
        deal.setExpectedCloseDate(request.expectedCloseDate());

        deal = dealRepository.save(deal);
        return toResponse(deal);
    }

    @Transactional
    public DealResponse update(UUID organizationId, UUID id, DealRequest request) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
        deal.setTitle(request.title());
        deal.setDescription(request.description());
        deal.setValue(request.value());

        if (request.pipelineId() != null) {
            Pipeline pipeline = pipelineRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.pipelineId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pipeline", "id", request.pipelineId()));
            deal.setPipeline(pipeline);
        }
        if (request.stageId() != null) {
            PipelineStage stage = pipelineStageRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(request.stageId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Stage", "id", request.stageId()));
            deal.setStage(stage);
        }
        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            deal.setClient(client);
        }
        if (request.contactId() != null) {
            Contact contact = contactRepository.findByIdAndOrganization_Id(request.contactId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", request.contactId()));
            deal.setContact(contact);
        }
        if (request.assignedToUserId() != null) {
            User assigned = userRepository.findById(request.assignedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));
            deal.setAssignedTo(assigned);
        } else {
            deal.setAssignedTo(null);
        }
        deal.setExpectedCloseDate(request.expectedCloseDate());

        deal = dealRepository.save(deal);
        return toResponse(deal);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Deal deal = dealRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
        deal.setDeletedAt(java.time.LocalDateTime.now());
        dealRepository.save(deal);
    }

    private DealResponse toResponse(Deal deal) {
        return new DealResponse(
                deal.getId(), deal.getTitle(), deal.getDescription(), deal.getValue(),
                deal.getPipeline() != null ? deal.getPipeline().getId() : null,
                deal.getPipeline() != null ? deal.getPipeline().getName() : null,
                deal.getStage() != null ? deal.getStage().getId() : null,
                deal.getStage() != null ? deal.getStage().getName() : null,
                deal.getClient() != null ? deal.getClient().getId() : null,
                deal.getClient() != null ? deal.getClient().getName() : null,
                deal.getContact() != null ? deal.getContact().getId() : null,
                deal.getAssignedTo() != null ? deal.getAssignedTo().getId() : null,
                deal.getAssignedTo() != null ? deal.getAssignedTo().getName() : null,
                deal.getExpectedCloseDate(), deal.getClosedAt(), deal.getWon(),
                deal.getCreatedAt(), deal.getUpdatedAt()
        );
    }
}
