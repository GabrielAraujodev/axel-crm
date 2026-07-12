package com.axelcrm.auth.security;

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_ORGANIZATION = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setOrganizationId(UUID organizationId) {
        CURRENT_ORGANIZATION.set(organizationId);
    }

    public static UUID getOrganizationId() {
        return CURRENT_ORGANIZATION.get();
    }

    public static UUID getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UUID id ? id : null;
    }

    public static void clear() {
        CURRENT_ORGANIZATION.remove();
    }
}
