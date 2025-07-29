package de.toxic2302.inventarbuddy.base.handler;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareHandler implements AuditorAware<String> {

    // ---- Functions ----
    @Override @NonNull
    public Optional<String> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || ! authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.ofNullable(String.valueOf(authentication.getName()));
    }
}
