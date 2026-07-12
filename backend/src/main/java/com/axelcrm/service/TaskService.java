package com.axelcrm.service;

import com.axelcrm.dto.TaskRequest;
import com.axelcrm.dto.TaskResponse;
import com.axelcrm.entity.Client;
import com.axelcrm.entity.Deal;
import com.axelcrm.entity.Lead;
import com.axelcrm.entity.Task;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ClientRepository;
import com.axelcrm.repository.DealRepository;
import com.axelcrm.repository.LeadRepository;
import com.axelcrm.repository.TaskRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;

    public Page<TaskResponse> findAll(UUID organizationId, Pageable pageable) {
        return taskRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public TaskResponse findById(UUID organizationId, UUID id) {
        return taskRepository.findByIdAndOrganization_Id(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    @Transactional
    public TaskResponse create(UUID organizationId, TaskRequest request) {
        User assigned = userRepository.findById(request.assignedToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status() != null ? request.status() : com.axelcrm.entity.enums.TaskStatus.PENDING);
        task.setDueDate(request.dueDate());
        task.setAssignedTo(assigned);

        if (task.getStatus() == com.axelcrm.entity.enums.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        if (request.leadId() != null) {
            Lead lead = leadRepository.findByIdAndOrganization_Id(request.leadId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", request.leadId()));
            task.setLead(lead);
        }
        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            task.setClient(client);
        }
        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));
            task.setDeal(deal);
        }

        task = taskRepository.save(task);
        return toResponse(task);
    }

    @Transactional
    public TaskResponse update(UUID organizationId, UUID id, TaskRequest request) {
        Task task = taskRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        User assigned = userRepository.findById(request.assignedToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.assignedToUserId()));

        task.setTitle(request.title());
        task.setDescription(request.description());
        
        com.axelcrm.entity.enums.TaskStatus oldStatus = task.getStatus();
        if (request.status() != null) {
            task.setStatus(request.status());
            if (request.status() == com.axelcrm.entity.enums.TaskStatus.COMPLETED && oldStatus != com.axelcrm.entity.enums.TaskStatus.COMPLETED) {
                task.setCompletedAt(LocalDateTime.now());
            } else if (request.status() != com.axelcrm.entity.enums.TaskStatus.COMPLETED) {
                task.setCompletedAt(null);
            }
        }
        
        task.setDueDate(request.dueDate());
        task.setAssignedTo(assigned);

        if (request.leadId() != null) {
            Lead lead = leadRepository.findByIdAndOrganization_Id(request.leadId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", request.leadId()));
            task.setLead(lead);
        } else {
            task.setLead(null);
        }

        if (request.clientId() != null) {
            Client client = clientRepository.findByIdAndOrganization_Id(request.clientId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.clientId()));
            task.setClient(client);
        } else {
            task.setClient(null);
        }

        if (request.dealId() != null) {
            Deal deal = dealRepository.findByIdAndOrganization_Id(request.dealId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", request.dealId()));
            task.setDeal(deal);
        } else {
            task.setDeal(null);
        }

        task = taskRepository.save(task);
        return toResponse(task);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Task task = taskRepository.findByIdAndOrganization_Id(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        task.setDeletedAt(java.time.LocalDateTime.now());
        taskRepository.save(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getCompletedAt(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null,
                task.getLead() != null ? task.getLead().getId() : null,
                task.getClient() != null ? task.getClient().getId() : null,
                task.getDeal() != null ? task.getDeal().getId() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
