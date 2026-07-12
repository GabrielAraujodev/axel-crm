package com.axelcrm.auth.service;

import com.axelcrm.auth.dto.UserRequest;
import com.axelcrm.auth.dto.UserResponse;
import com.axelcrm.auth.entity.User;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.commons.entity.enums.Role;
import com.axelcrm.commons.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> findAll(UUID organizationId, Pageable pageable) {
        return userRepository.findByOrganization_IdAndDeletedAtIsNull(organizationId, pageable)
                .map(this::toResponse);
    }

    public UserResponse findById(UUID organizationId, UUID id) {
        return userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public UserResponse create(UUID organizationId, UserRequest request) {
        User user = new User();
        user.setName(request.fullName());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setRole(request.role() != null ? request.role() : Role.USER);
        user.setActive(request.active() != null ? request.active() : true);
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public UserResponse update(UUID organizationId, UUID id, UserRequest request) {
        User user = userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setName(request.fullName());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setRole(request.role());
        user.setActive(request.active());
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void delete(UUID organizationId, UUID id) {
        User user = userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(id, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(), user.getName(), user.getEmail(),
                user.getRole(), user.isActive(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getOrganization() != null ? user.getOrganization().getName() : null,
                null, user.getCreatedAt()
        );
    }
}
