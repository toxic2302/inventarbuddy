package de.toxic2302.inventarbuddy.base.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

class KeycloakRoleConverterTest {

    private KeycloakRoleConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakRoleConverter();
    }

    @Test
    void convert_shouldExtractRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("realm_access", Map.of("roles", List.of("user", "admin")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void convert_shouldExtractClientRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("resource_access", Map.of(
                        "my-client", Map.of("roles", List.of("editor", "viewer"))
                ))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_EDITOR", "ROLE_VIEWER");
    }

    @Test
    void convert_shouldCombineRealmAndClientRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("realm_access", Map.of("roles", List.of("user")))
                .claim("resource_access", Map.of(
                        "client-a", Map.of("roles", List.of("editor"))
                ))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_EDITOR");
    }

    @Test
    void convert_shouldReturnEmptyWhenNoRoleClaims() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void convert_shouldHandleEmptyRealmRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("realm_access", Map.of("roles", List.of()))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    void convert_shouldHandleMultipleClients() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("resource_access", Map.of(
                        "client-a", Map.of("roles", List.of("role-a")),
                        "client-b", Map.of("roles", List.of("role-b"))
                ))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ROLE-A", "ROLE_ROLE-B");
    }

    @Test
    void convert_shouldUppercaseRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user-1")
                .claim("realm_access", Map.of("roles", List.of("mixedCase")))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_MIXEDCASE");
    }
}
