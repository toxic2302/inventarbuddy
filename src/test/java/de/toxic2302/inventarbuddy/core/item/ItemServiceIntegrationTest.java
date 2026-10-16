package de.toxic2302.inventarbuddy.core.item;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.repository.ItemRepository;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.media.repository.MediaRepository;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.repository.StorageRepository;
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
class ItemServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        mediaRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        storageRepository.deleteAll();
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
        mediaRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        storageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createItem_shouldPersistAllProperties() {
        final ItemDto request = new ItemDto(null, "Drill", "Cordless drill", "SN-1", "GSR 18V", "Bosch",
                3, true, null, null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("Drill");
                    assertThat(response.modelNumber()).isEqualTo("GSR 18V");
                    assertThat(response.manufacturer()).isEqualTo("Bosch");
                    assertThat(response.quantity()).isEqualTo(3);
                    assertThat(response.sold()).isTrue();
                });

        final List<Item> items = itemRepository.findAll();
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(items.getFirst().getQuantity()).isEqualTo(3);
        assertThat(items.getFirst().isSold()).isTrue();
    }

    @Test
    void createItem_shouldApplyDefaultsForQuantityAndSold() {
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, null, null, null, null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.quantity()).isEqualTo(1);
                    assertThat(response.sold()).isFalse();
                });
    }

    @Test
    void createItem_shouldReturn400WhenNameIsBlank() {
        final ItemDto request = new ItemDto(null, "", null, null, null, null, 1, false, null, null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createItem_shouldReturn400WhenQuantityIsNegative() {
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, -1, false, null, null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createItem_shouldReturn401WhenNotAuthenticated() {
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, 1, false, null, null, null);

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getAllItems_shouldReturnOnlyItemsOfAuthenticatedUser() {
        createItem("Drill", testUser);
        createItem("Saw", testUser);
        createItem("Foreign", createOtherUser());

        webTestClient.get()
                .uri(addBaseUrl("/items"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ItemDto.class)
                .value(items -> assertThat(items).extracting(ItemDto::name)
                        .containsExactlyInAnyOrder("Drill", "Saw"));
    }

    @Test
    void getItemById_shouldReturnItem() {
        final Item saved = createItem("Drill", testUser);

        webTestClient.get()
                .uri(addBaseUrl("/items/" + saved.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(saved.getId().toString());
                    assertThat(response.manufacturer()).isEqualTo("Bosch");
                    assertThat(response.quantity()).isEqualTo(2);
                });
    }

    @Test
    void getItemById_shouldNotReturnItemOfOtherUser() {
        final Item foreign = createItem("Foreign", createOtherUser());

        webTestClient.get()
                .uri(addBaseUrl("/items/" + foreign.getId()))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void updateItem_shouldUpdatePropertiesAndKeepUser() {
        final Item saved = createItem("Drill", testUser);
        final ItemDto request = new ItemDto(saved.getId().toString(), "Drill 2", "New", "SN-2", "M-2", "Makita",
                7, true, null, null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items/update"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.name()).isEqualTo("Drill 2");
                    assertThat(response.modelNumber()).isEqualTo("M-2");
                    assertThat(response.manufacturer()).isEqualTo("Makita");
                    assertThat(response.quantity()).isEqualTo(7);
                    assertThat(response.sold()).isTrue();
                });

        final Item updated = itemRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void deleteItem_shouldRemoveItem() {
        final Item saved = createItem("Drill", testUser);

        webTestClient.delete()
                .uri(addBaseUrl("/items/delete/" + saved.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(itemRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void getItem_shouldReturnListOfMedia() {
        final Item saved = createItem("Drill", testUser);
        for (String fileName : List.of("manual.pdf", "photo.png")) {
            final Media media = new Media();
            media.setFileName(fileName);
            media.setContentType(fileName.endsWith(".pdf") ? "application/pdf" : "image/png");
            media.setFileSize(2);
            media.setData(new byte[]{1, 2});
            media.setItem(saved);
            media.setUser(testUser);
            mediaRepository.save(media);
        }

        webTestClient.get()
                .uri(addBaseUrl("/items/" + saved.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> assertThat(response.media())
                        .extracting(MediaDto::fileName)
                        .containsExactlyInAnyOrder("manual.pdf", "photo.png"));
    }

    @Test
    void deleteItem_shouldAlsoRemoveItsMedia() {
        final Item saved = createItem("Drill", testUser);
        final Media media = new Media();
        media.setFileName("manual.pdf");
        media.setContentType("application/pdf");
        media.setFileSize(2);
        media.setData(new byte[]{1, 2});
        media.setItem(saved);
        media.setUser(testUser);
        mediaRepository.save(media);

        webTestClient.delete()
                .uri(addBaseUrl("/items/delete/" + saved.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(itemRepository.findById(saved.getId())).isEmpty();
        assertThat(mediaRepository.findAll()).isEmpty();
    }

    @Test
    void createItem_shouldAssignCategoryAndStorage() {
        final Category category = createCategory("Tools", testUser);
        final Storage storage = createStorage("Garage", testUser);
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, 1, false,
                category.getId().toString(), storage.getId().toString(), null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.categoryId()).isEqualTo(category.getId().toString());
                    assertThat(response.storageId()).isEqualTo(storage.getId().toString());
                });
    }

    @Test
    void createItem_shouldRejectCategoryOfOtherUser() {
        final Category foreign = createCategory("Foreign", createOtherUser());
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, 1, false,
                foreign.getId().toString(), null, null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(itemRepository.findAll()).isEmpty();
    }

    @Test
    void createItem_shouldRejectStorageOfOtherUser() {
        final Storage foreign = createStorage("Foreign", createOtherUser());
        final ItemDto request = new ItemDto(null, "Drill", null, null, null, null, 1, false,
                null, foreign.getId().toString(), null);

        webTestClient.post()
                .uri(addBaseUrl("/items"))
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(itemRepository.findAll()).isEmpty();
    }

    @Test
    void updateItem_shouldChangeAndClearCategoryAndStorage() {
        final Category category = createCategory("Tools", testUser);
        final Storage storage = createStorage("Garage", testUser);
        final Item saved = createItem("Drill", testUser);

        webTestClient.post()
                .uri(addBaseUrl("/items/update"))
                .bodyValue(new ItemDto(saved.getId().toString(), "Drill", null, null, null, null, 1, false,
                        category.getId().toString(), storage.getId().toString(), null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.categoryId()).isEqualTo(category.getId().toString());
                    assertThat(response.storageId()).isEqualTo(storage.getId().toString());
                });

        webTestClient.post()
                .uri(addBaseUrl("/items/update"))
                .bodyValue(new ItemDto(saved.getId().toString(), "Drill", null, null, null, null, 1, false,
                        null, null, null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemDto.class)
                .value(response -> {
                    assertThat(response.categoryId()).isNull();
                    assertThat(response.storageId()).isNull();
                });
    }

    private Category createCategory(String name, User owner) {
        final Category category = new Category();
        category.setName(name);
        category.setUser(owner);
        return categoryRepository.save(category);
    }

    private Storage createStorage(String name, User owner) {
        final Storage storage = new Storage();
        storage.setName(name);
        storage.setUser(owner);
        return storageRepository.save(storage);
    }

    private User createOtherUser() {
        final User other = new User();
        other.setKeycloakId("other-user");
        other.setEmail("other@example.com");
        other.setFirstName("Other");
        other.setLastName("User");
        return userRepository.save(other);
    }

    private Item createItem(String name, User owner) {
        final Item item = new Item();
        item.setName(name);
        item.setManufacturer("Bosch");
        item.setQuantity(2);
        item.setUser(owner);
        return itemRepository.save(item);
    }
}
