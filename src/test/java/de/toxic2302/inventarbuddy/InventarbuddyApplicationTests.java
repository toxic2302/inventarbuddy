package de.toxic2302.inventarbuddy;

import de.toxic2302.inventarbuddy.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@SpringBootTest
class InventarbuddyApplicationTests {

    @Test
    void contextLoads() {
    }
}
