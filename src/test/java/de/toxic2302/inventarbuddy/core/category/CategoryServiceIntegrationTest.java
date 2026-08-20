package de.toxic2302.inventarbuddy.core.category;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CategoryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setKeycloakId("test-user");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createCategory_shouldPersistAndReturnCategory() {
        CategoryDto request = new CategoryDto(null, "Elektronik", "Elektronische Geräte");

        webTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("Elektronik");
                    assertThat(response.description()).isEqualTo("Elektronische Geräte");
                });

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getName()).isEqualTo("Elektronik");
    }

    @Test
    void getAllCategories_shouldReturnCategoriesForAuthenticatedUser() {
        Category category1 = new Category("Elektronik", "Elektronische Geräte", testUser);
        Category category2 = new Category("Möbel", "Möbelstücke", testUser);
        categoryRepository.saveAll(List.of(category1, category2));

        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryDto.class)
                .hasSize(2)
                .value(categories -> {
                    assertThat(categories).extracting(CategoryDto::name)
                            .containsExactlyInAnyOrder("Elektronik", "Möbel");
                });
    }

    @Test
    void getAllCategories_shouldReturnEmptyListWhenNoCategories() {
        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryDto.class)
                .hasSize(0);
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        Category category = new Category("Elektronik", "Elektronische Geräte", testUser);
        final Category savedCategory = categoryRepository.save(category);

        webTestClient.post()
                .uri(addBaseUrl("/categories/" + savedCategory.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(savedCategory.getId().toString());
                    assertThat(response.name()).isEqualTo("Elektronik");
                    assertThat(response.description()).isEqualTo("Elektronische Geräte");
                });
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategory() {
        Category category = new Category("Elektronik", "Elektronische Geräte", testUser);
        final Category savedCategory = categoryRepository.save(category);

        CategoryDto updateRequest = new CategoryDto(savedCategory.getId().toString(), "Elektronik Updated", "Neue Beschreibung");

        webTestClient.post()
                .uri(addBaseUrl("/categories/update"))
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryDto.class)
                .value(response -> {
                    assertThat(response.name()).isEqualTo("Elektronik Updated");
                    assertThat(response.description()).isEqualTo("Neue Beschreibung");
                });
    }

    @Test
    void deleteCategory_shouldRemoveCategory() {
        Category category = new Category("Elektronik", "Elektronische Geräte", testUser);
        final Category savedCategory = categoryRepository.save(category);

        webTestClient.delete()
                .uri(addBaseUrl("/categories/delete/" + savedCategory.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(categoryRepository.findById(savedCategory.getId())).isEmpty();
    }

    @Test
    void createCategory_shouldReturn401WhenNotAuthenticated() {
        CategoryDto request = new CategoryDto(null, "Elektronik", "Elektronische Geräte");

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createCategory_shouldReturn400WhenNameIsBlank() {
        CategoryDto request = new CategoryDto(null, "", "Beschreibung");

        webTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
