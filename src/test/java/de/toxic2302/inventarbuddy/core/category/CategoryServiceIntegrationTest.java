package de.toxic2302.inventarbuddy.core.category;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryChildDto;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryParentDto;
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
        CategoryChildDto request = new CategoryChildDto(null, "Elektronik", "Elektronische Geräte", null);

        webTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryChildDto.class)
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
        createCategory("Elektronik", "Elektronische Geräte", null);
        createCategory("Möbel", "Möbelstücke", null);

        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryParentDto.class)
                .hasSize(2)
                .value(categories -> {
                    assertThat(categories).extracting(CategoryParentDto::name)
                            .containsExactlyInAnyOrder("Elektronik", "Möbel");
                });
    }

    @Test
    void getAllCategories_shouldReturnEmptyListWhenNoCategories() {
        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryParentDto.class)
                .hasSize(0);
    }

    @Test
    void getCategoryById_shouldReturnCategory() {
        final Category savedCategory = createCategory("Elektronik", "Elektronische Geräte", null);

        webTestClient.post()
                .uri(addBaseUrl("/categories/" + savedCategory.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryParentDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(savedCategory.getId().toString());
                    assertThat(response.name()).isEqualTo("Elektronik");
                    assertThat(response.description()).isEqualTo("Elektronische Geräte");
                });
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategory() {
        final Category savedCategory = createCategory("Elektronik", "Elektronische Geräte", null);

        CategoryChildDto updateRequest = new CategoryChildDto(savedCategory.getId().toString(), "Elektronik Updated", "Neue Beschreibung", null);

        webTestClient.post()
                .uri(addBaseUrl("/categories/update"))
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryChildDto.class)
                .value(response -> {
                    assertThat(response.name()).isEqualTo("Elektronik Updated");
                    assertThat(response.description()).isEqualTo("Neue Beschreibung");
                });
    }

    @Test
    void deleteCategory_shouldRemoveCategory() {
        final Category savedCategory = createCategory("Elektronik", "Elektronische Geräte", null);

        webTestClient.delete()
                .uri(addBaseUrl("/categories/delete/" + savedCategory.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(categoryRepository.findById(savedCategory.getId())).isEmpty();
    }

    @Test
    void createCategory_shouldReturn401WhenNotAuthenticated() {
        CategoryChildDto request = new CategoryChildDto(null, "Elektronik", "Elektronische Geräte", null);

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createCategory_shouldReturn400WhenNameIsBlank() {
        CategoryChildDto request = new CategoryChildDto(null, "", "Beschreibung", null);

        webTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createCategory_withParent_shouldLinkChildToParent() {
        final Category parent = createCategory("Elektronik", null, null);
        CategoryChildDto request = new CategoryChildDto(null, "Handys", "Mobiltelefone", parent.getId().toString());

        webTestClient.post()
                .uri(addBaseUrl("/categories"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryChildDto.class)
                .value(response -> assertThat(response.parentId()).isEqualTo(parent.getId().toString()));

        assertThat(categoryRepository.findById(parent.getId()).orElseThrow().getChildren())
                .extracting(Category::getName)
                .containsExactly("Handys");
    }

    @Test
    void getAllCategories_shouldReturnOnlyRootCategoriesWithChildren() {
        final Category parent = createCategory("Elektronik", null, null);
        createCategory("Handys", null, parent);

        webTestClient.get()
                .uri(addBaseUrl("/categories"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryParentDto.class)
                .hasSize(1)
                .value(categories -> {
                    assertThat(categories.getFirst().name()).isEqualTo("Elektronik");
                    assertThat(categories.getFirst().children()).extracting(CategoryParentDto::name)
                            .containsExactly("Handys");
                });
    }

    @Test
    void deleteCategory_shouldDeleteChildCategoryOnFirstCall() {
        final Category parent = createCategory("Elektronik", null, null);
        final Category child = createCategory("Handys", null, parent);

        webTestClient.delete()
                .uri(addBaseUrl("/categories/delete/" + child.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(categoryRepository.findById(child.getId())).isEmpty();
        assertThat(categoryRepository.findById(parent.getId())).isPresent();
        assertThat(categoryRepository.findById(parent.getId()).orElseThrow().getChildren()).isEmpty();
    }

    @Test
    void deleteCategory_shouldDeleteParentWithChildrenOnFirstCall() {
        final Category parent = createCategory("Elektronik", null, null);
        final Category child = createCategory("Handys", null, parent);
        final Category grandChild = createCategory("Smartphones", null, child);

        webTestClient.delete()
                .uri(addBaseUrl("/categories/delete/" + parent.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(categoryRepository.findById(parent.getId())).isEmpty();
        assertThat(categoryRepository.findById(child.getId())).isEmpty();
        assertThat(categoryRepository.findById(grandChild.getId())).isEmpty();
    }

    private Category createCategory(String name, String description, Category parent) {
        final Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setUser(testUser);
        if (parent != null) {
            final Category managedParent = categoryRepository.findById(parent.getId()).orElseThrow();
            managedParent.addChild(category);
        }
        return categoryRepository.save(category);
    }
}
