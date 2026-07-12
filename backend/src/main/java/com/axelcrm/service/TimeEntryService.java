package com.axelcrm.service;

import com.axelcrm.dto.TimeEntryRequest;
import com.axelcrm.dto.TimeEntryResponse;
import com.axelcrm.entity.Project;
import com.axelcrm.entity.Task;
import com.axelcrm.entity.TimeEntry;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.ProjectRepository;
import com.axelcrm.repository.TaskRepository;
import com.axelcrm.repository.TimeEntryRepository;
import com.axelcrm.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public Page<TimeEntryResponse> findAll(UUID organizationId, Pageable pageable) {
        return timeEntryRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public TimeEntryResponse findById(UUID organizationId, UUID id) {
        return timeEntryRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("TimeEntry", "id", id));
    }

    @Transactional
    public TimeEntryResponse create(UUID organizationId, TimeEntryRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setStartTime(request.startTime());
        entry.setEndTime(request.endTime());
        entry.setDescription(request.description());
        entry.setHourlyRate(request.hourlyRate());

        Integer duration = request.durationMinutes();
        if (duration == null && request.startTime() != null && request.endTime() != null) {
            duration = (int) Duration.between(request.startTime(), request.endTime()).toMinutes();
        }
        entry.setDurationMinutes(duration);

        if (request.taskId() != null) {
            Task task = taskRepository.findByIdAndOrganization_Id(request.taskId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", request.taskId()));
            entry.setTask(task);
        }
        if (request.projectId() != null) {
            Project project = projectRepository.findByIdAndOrganization_Id(request.projectId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.projectId()));
            entry.setProject(project);
        }

        entry = timeEntryRepository.save(entry);
        return toResponse(entry);
    }

    @Transactional
    public TimeEntryResponse update(UUID organizationId, UUID id, TimeEntryRequest request) {
        TimeEntry entry = timeEntryRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("TimeEntry", "id", id));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        entry.setUser(user);
        entry.setStartTime(request.startTime());
        entry.setEndTime(request.endTime());
        entry.setDescription(request.description());
        entry.setHourlyRate(request.hourlyRate());

        Integer duration = request.durationMinutes();
        if (duration == null && request.startTime() != null && request.endTime() != null) {
            duration = (int) Duration.between(request.startTime(), request.endTime()).toMinutes();
        }
        entry.setDurationMinutes(duration);

        if (request.taskId() != null) {
            Task task = taskRepository.findByIdAndOrganization_Id(request.taskId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", request.taskId()));
            entry.setTask(task);
        } else {
            entry.setTask(null);
        }

        if (request.projectId() != null) {
            Project project = projectRepository.findByIdAndOrganization_Id(request.projectId(), organizationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.projectId()));
            entry.setProject(project);
        } else {
            entry.setProject(null);
        }

        entry = timeEntryRepository.save(entry);
        return toResponse(entry);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        TimeEntry entry = timeEntryRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("TimeEntry", "id", id));
        entry.setDeletedAt(java.time.LocalDateTime.now());
        timeEntryRepository.save(entry);
    }

    private TimeEntryResponse toResponse(TimeEntry entry) {
        return new TimeEntryResponse(
                entry.getId(),
                entry.getUser() != null ? entry.getUser().getId() : null,
                entry.getUser() != null ? entry.getUser().getName() : null,
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getDurationMinutes(),
                entry.getDescription(),
                entry.getTask() != null ? entry.getTask().getId() : null,
                entry.getTask() != null ? entry.getTask().getTitle() : null,
                entry.getProject() != null ? entry.getProject().getId() : null,
                entry.getProject() != null ? entry.getProject().getName() : null,
                entry.getHourlyRate(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
