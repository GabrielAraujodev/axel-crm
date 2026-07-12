package com.axelcrm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.axelcrm.auth.dto.AuthRequest;
import com.axelcrm.auth.dto.LoginResponse;
import com.axelcrm.auth.dto.RegisterRequest;
import com.axelcrm.commons.entity.Organization;
import com.axelcrm.auth.entity.User;
import com.axelcrm.commons.entity.enums.Role;
import com.axelcrm.auth.repository.OrganizationRepository;
import com.axelcrm.auth.repository.UserRepository;
import com.axelcrm.auth.security.JwtUtil;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.axelcrm.auth.service.AuthService;
import com.axelcrm.commons.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    OrganizationRepository organizationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthService authService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final String rawPassword = "rawPass123";

    @Test
    void register_ShouldCreateOrgAndUserAndReturnToken() {
        var request = new RegisterRequest("MyOrg", "John", "john@test.com", rawPassword);
        var savedOrg = new Organization();
        savedOrg.setId(orgId);
        savedOrg.setName("MyOrg");

        var savedUser = new User();
        savedUser.setId(userId);
        savedUser.setName("John");
        savedUser.setEmail("john@test.com");
        savedUser.setRole(Role.ADMIN);
        savedUser.setActive(true);
        savedUser.setOrganization(savedOrg);

        when(userRepository.findByEmailAndDeletedAtIsNull("john@test.com")).thenReturn(Optional.empty());
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrg);
        when(passwordEncoder.encode(rawPassword)).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwt-token");

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(userId, response.userId());
        assertEquals("John", response.userName());
        assertEquals("john@test.com", response.email());
        assertEquals("ADMIN", response.role());
        assertEquals(orgId, response.organizationId());
        assertEquals("MyOrg", response.organizationName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("hashed", userCaptor.getValue().getPassword());
    }

    @Test
    void register_ShouldThrowWhenEmailAlreadyExists() {
        var request = new RegisterRequest("Org", "John", "existing@test.com", rawPassword);
        when(userRepository.findByEmailAndDeletedAtIsNull("existing@test.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(organizationRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnTokenWhenCredentialsValid() {
        var request = new AuthRequest("john@test.com", rawPassword);
        var org = new Organization();
        org.setId(orgId);
        org.setName("MyOrg");

        var user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setPassword("hashed");
        user.setRole(Role.ADMIN);
        user.setActive(true);
        user.setOrganization(org);

        when(userRepository.findByEmailAndDeletedAtIsNull("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals(userId, response.userId());
    }

    @Test
    void login_ShouldThrowWhenPasswordDoesNotMatch() {
        var request = new AuthRequest("john@test.com", "wrong");
        var user = new User();
        user.setPassword("hashed");

        when(userRepository.findByEmailAndDeletedAtIsNull("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_ShouldThrowWhenUserInactive() {
        var request = new AuthRequest("john@test.com", rawPassword);
        var user = new User();
        user.setEmail("john@test.com");
        user.setPassword("hashed");
        user.setActive(false);

        when(userRepository.findByEmailAndDeletedAtIsNull("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "hashed")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }
}
