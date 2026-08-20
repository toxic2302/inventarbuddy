package de.toxic2302.inventarbuddy.security;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class SecurityTest extends BaseIntegrationTest {

    @Test
    void authenticatedUser_shouldAccessCategories() {
        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void anonymousUser_shouldGetUnauthorizedOnCategories() {
        anonymousWebTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void anonymousUser_shouldGetUnauthorizedOnCreateCategory() {
        CategoryDto request = new CategoryDto(null, "Test", "Beschreibung");

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void anonymousUser_shouldGetUnauthorizedOnDeleteCategory() {
        anonymousWebTestClient.delete()
                .uri(addBaseUrl("/categories/delete/00000000-0000-0000-0000-000000000001"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void anonymousUser_shouldGetUnauthorizedOnUpdateCategory() {
        CategoryDto request = new CategoryDto("00000000-0000-0000-0000-000000000001", "Updated", "Beschreibung");

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/categories/update"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void adminUser_shouldAccessCategories() {
        adminWebTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk();
    }
}
