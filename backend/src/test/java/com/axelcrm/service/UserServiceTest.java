package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.auth.dto.UserRequest;
import com.axelcrm.auth.dto.UserResponse;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.entity.enums.Role;
import com.axelcrm.commons.exception.ResourceNotFoundException;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import com.axelcrm.auth.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private User createUser() {
        var org = new Organization();
        org.setId(orgId);
        org.setName("Org");

        var user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setPassword("encoded");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setOrganization(org);
        return user;
    }

    @Test
    void findAll_ShouldReturnPagedUsers() {
        var user = createUser();
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(java.util.List.of(user));

        when(userRepository.findByOrganization_IdAndDeletedAtIsNull(orgId, pageable)).thenReturn(page);

        Page<UserResponse> result = userService.findAll(orgId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(userId, result.getContent().getFirst().id());
        assertEquals("John", result.getContent().getFirst().fullName());
    }

    @Test
    void findById_ShouldReturnUser() {
        var user = createUser();
        when(userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(userId, orgId))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.findById(orgId, userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("john@test.com", result.email());
        assertEquals(orgId, result.organizationId());
    }

    @Test
    void findById_ShouldThrowWhenNotFound() {
        when(userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(userId, orgId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(orgId, userId));
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        var request = new UserRequest("Jane", "jane@test.com", "secret", Role.USER, true);
        var saved = new User();
        saved.setId(UUID.randomUUID());
        saved.setName("Jane");
        saved.setEmail("jane@test.com");
        saved.setRole(Role.USER);
        saved.setActive(true);

        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse result = userService.create(orgId, request);

        assertNotNull(result);
        assertEquals("Jane", result.fullName());
        assertEquals("jane@test.com", result.email());
    }

    @Test
    void update_ShouldModifyAndReturnUser() {
        var request = new UserRequest("Jane Updated", "jane@test.com", null, Role.ADMIN, true);
        var existing = createUser();

        when(userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(userId, orgId))
                .thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse result = userService.update(orgId, userId, request);

        assertNotNull(result);
        assertEquals("Jane Updated", result.fullName());
    }

    @Test
    void delete_ShouldSetDeletedAt() {
        var user = createUser();
        when(userRepository.findByIdAndOrganization_IdAndDeletedAtIsNull(userId, orgId))
                .thenReturn(Optional.of(user));

        userService.delete(orgId, userId);

        assertNotNull(user.getDeletedAt());
        verify(userRepository).save(user);
    }
}
