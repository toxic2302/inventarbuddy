package de.toxic2302.inventarbuddy.security;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.config.TestSecurityConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class SecurityTest extends BaseIntegrationTest {
}
