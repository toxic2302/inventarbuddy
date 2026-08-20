package de.toxic2302.inventarbuddy.config;

import java.time.Instant;
import java.util.List;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.vault.core.VaultTemplate;

@TestConfiguration
public class TestSecurityConfig {

    public static final String USER_TOKEN = "user.jwt.token";
    public static final String ADMIN_TOKEN = "admin.jwt.token";

    @Bean
    @Primary
    public VaultTemplate vaultTemplate() {
        return Mockito.mock(VaultTemplate.class);
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {

        return token -> {

            // Determine roles based on token
            List<String> permissions = switch (token) {
                case ADMIN_TOKEN -> List.of("ROLE_USER", "ROLE_ADMIN");
                case USER_TOKEN -> List.of("ROLE_USER");
                default -> List.of("ROLE_USER"); // fallback
            };

            return Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .header("typ", "JWT")
                    .subject("test-user")
                    .claim("permissions", permissions)  // Match your PERMISSIONS_CLAIM
                    .claim("aud", List.of("test-audience"))
                    .issuer("http://localhost")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .build();
        };
    }
}
