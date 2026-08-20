package de.toxic2302.inventarbuddy.base;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import de.toxic2302.inventarbuddy.config.TestSecurityConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
public abstract class BaseIntegrationTest {

    @Value("${spring.mvc.servlet.path:}")
    protected String basePath;

    protected WebTestClient webTestClient;

    protected WebTestClient adminWebTestClient;

    protected WebTestClient anonymousWebTestClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void initWebTestClients() {
        WebTestClient baseClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        this.webTestClient = createAuthenticatedClient(baseClient, TestSecurityConfig.USER_TOKEN);

        this.adminWebTestClient = createAuthenticatedClient(baseClient, TestSecurityConfig.ADMIN_TOKEN);

        this.anonymousWebTestClient = baseClient.mutate()
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .build();
    }

    protected String addBaseUrl(String path) {
        return basePath + path;
    }

    private WebTestClient createAuthenticatedClient(WebTestClient baseClient, String token) {
        return baseClient.mutate()
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }
}
