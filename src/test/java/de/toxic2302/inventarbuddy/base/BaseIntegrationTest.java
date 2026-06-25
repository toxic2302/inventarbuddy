package de.toxic2302.inventarbuddy.base;

import tools.jackson.databind.ObjectMapper;
import de.toxic2302.inventarbuddy.config.TestSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected static RequestPostProcessor jwt() {
        return jwtWithSubject(UUID.randomUUID().toString(), List.of("USER"));
    }

    protected static RequestPostProcessor jwt(String... roles) {
        return jwtWithSubject(UUID.randomUUID().toString(), List.of(roles));
    }

    protected static RequestPostProcessor jwtWithSubject(String subject, List<String> roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder
                        .subject(subject)
                        .claim("email", "test@example.com")
                        .claim("given_name", "Test")
                        .claim("family_name", "User")
                        .claim("realm_access", Map.of("roles", roles))
                );
    }
}
