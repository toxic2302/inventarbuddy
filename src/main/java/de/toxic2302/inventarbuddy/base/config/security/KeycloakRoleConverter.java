package de.toxic2302.inventarbuddy.base.config.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Extract realm roles
        final Stream<String> realmRoles = extractRealmRoles(jwt);

        // Extract client roles (all clients)
        final Stream<String> clientRoles = extractClientRoles(jwt);

        // Combine and prefix with ROLE_ for Spring Security
        return Stream.concat(realmRoles, clientRoles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toSet());
    }

    private Stream<String> extractRealmRoles(Jwt jwt) {
        final Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null) {
            return Stream.empty();
        }

        @SuppressWarnings("unchecked")
        final List<String> roles = (List<String>) realmAccess.get("roles");

        return roles != null ? roles.stream() : Stream.empty();
    }

    private Stream<String> extractClientRoles(Jwt jwt) {
        final Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (resourceAccess == null) {
            return Stream.empty();
        }

        return resourceAccess.values().stream()
                .filter(v -> v instanceof Map)
                .flatMap(clientAccess -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> access = (Map<String, Object>) clientAccess;
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) access.get("roles");
                    return roles != null ? roles.stream() : Stream.empty();
                });
    }
}
