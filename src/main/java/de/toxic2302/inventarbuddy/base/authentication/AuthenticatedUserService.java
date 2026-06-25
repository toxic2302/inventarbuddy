package de.toxic2302.inventarbuddy.base.authentication;

import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserService userService;

    @Transactional
    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            final String keycloakId = jwt.getSubject();

            return userService.findByKeycloakId(keycloakId)
                    .orElseGet(() -> createUserFromJwt(jwt));
        }

        throw new IllegalStateException("No authenticated user found in security context");
    }

    private User createUserFromJwt(Jwt jwt) {
        final User user = new User();
        user.setKeycloakId(jwt.getSubject());
        user.setEmail(jwt.getClaimAsString("email"));
        user.setFirstName(jwt.getClaimAsString("given_name"));
        user.setLastName(jwt.getClaimAsString("family_name"));
        return userService.saveUser(user);
    }
}
