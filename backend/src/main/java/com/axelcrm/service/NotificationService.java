package com.axelcrm.service;

import com.axelcrm.dto.NotificationRequest;
import com.axelcrm.dto.NotificationResponse;
import com.axelcrm.entity.Notification;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import com.axelcrm.repository.NotificationRepository;
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
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<NotificationResponse> findAll(UUID organizationId, Pageable pageable) {
        return notificationRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public NotificationResponse findById(UUID organizationId, UUID id) {
        return notificationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
    }

    @Transactional
    public NotificationResponse create(UUID organizationId, NotificationRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.userId()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setEntityType(request.entityType());
        notification.setEntityId(request.entityId());
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Transactional
    public NotificationResponse markAsRead(UUID organizationId, UUID id) {
        Notification notification = notificationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        Notification notification = notificationRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getUser() != null ? notification.getUser().getName() : null,
                notification.getTitle(),
                notification.getMessage(),
                notification.getEntityType(),
                notification.getEntityId(),
                notification.getReadAt(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
