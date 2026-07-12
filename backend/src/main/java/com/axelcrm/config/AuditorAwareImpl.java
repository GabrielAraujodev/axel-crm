package com.axelcrm.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves the current auditor identifier from the security context.
 */
@SuppressWarnings("null")
public class AuditorAwareImpl implements AuditorAware<UUID>
{

    @Override
    @org.jspecify.annotations.NonNull
    public Optional<UUID> getCurrentAuditor()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
        {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID userId)
        {
            return Optional.of(userId);
        }

        return Optional.empty();
    }

}
