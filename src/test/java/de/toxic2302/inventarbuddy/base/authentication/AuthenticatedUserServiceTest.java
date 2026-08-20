package de.toxic2302.inventarbuddy.base.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.service.UserService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_shouldReturnExistingUser() {
        Jwt jwt = createJwt("keycloak-123");
        User existingUser = createUser("keycloak-123");

        setupSecurityContext(jwt);
        when(userService.findByKeycloakId("keycloak-123")).thenReturn(Optional.of(existingUser));

        User result = authenticatedUserService.getCurrentUser();

        assertThat(result).isEqualTo(existingUser);
        verify(userService).findByKeycloakId("keycloak-123");
    }

    @Test
    void getCurrentUser_shouldCreateNewUserWhenNotFound() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("new-keycloak-id")
                .claim("email", "new@example.com")
                .claim("given_name", "New")
                .claim("family_name", "User")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        User savedUser = createUser("new-keycloak-id");

        setupSecurityContext(jwt);
        when(userService.findByKeycloakId("new-keycloak-id")).thenReturn(Optional.empty());
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        User result = authenticatedUserService.getCurrentUser();

        assertThat(result).isEqualTo(savedUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getKeycloakId()).isEqualTo("new-keycloak-id");
        assertThat(capturedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(capturedUser.getFirstName()).isEqualTo("New");
        assertThat(capturedUser.getLastName()).isEqualTo("User");
    }

    @Test
    void getCurrentUser_shouldThrowWhenNoAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThatThrownBy(() -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found in security context");
    }

    @Test
    void getCurrentUser_shouldThrowWhenPrincipalIsNotJwt() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-a-jwt");

        assertThatThrownBy(() -> authenticatedUserService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found in security context");
    }

    private Jwt createJwt(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private User createUser(String keycloakId) {
        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        return user;
    }

    private void setupSecurityContext(Jwt jwt) {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
    }
}
