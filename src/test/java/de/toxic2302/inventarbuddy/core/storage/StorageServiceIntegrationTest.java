package de.toxic2302.inventarbuddy.core.storage;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
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
class StorageServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
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
        storageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createStorage_shouldPersistAndReturnStorage() {
        StorageChildDto request = new StorageChildDto(null, "Keller", "Lagerraum im Haus", null);

        webTestClient.post()
                .uri(addBaseUrl("/storages"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StorageChildDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.name()).isEqualTo("Keller");
                    assertThat(response.description()).isEqualTo("Lagerraum im Haus");
                });

        List<Storage> storages = storageRepository.findAll();
        assertThat(storages).hasSize(1);
        assertThat(storages.getFirst().getName()).isEqualTo("Keller");
    }

    @Test
    void getAllStorages_shouldReturnStoragesForAuthenticatedUser() {
        createStorage("Keller", "Lagerraum im Haus", null);
        createStorage("Garage", "Außenlager", null);

        webTestClient.get()
                .uri(addBaseUrl("/storages"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StorageParentDto.class)
                .hasSize(2)
                .value(storages -> {
                    assertThat(storages).extracting(StorageParentDto::name)
                            .containsExactlyInAnyOrder("Keller", "Garage");
                });
    }

    @Test
    void getAllStorages_shouldReturnEmptyListWhenNoStorages() {
        webTestClient.get()
                .uri(addBaseUrl("/storages"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StorageParentDto.class)
                .hasSize(0);
    }

    @Test
    void getStorageById_shouldReturnStorage() {
        final Storage savedStorage = createStorage("Keller", "Lagerraum im Haus", null);

        webTestClient.post()
                .uri(addBaseUrl("/storages/" + savedStorage.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StorageParentDto.class)
                .value(response -> {
                    assertThat(response.id()).isEqualTo(savedStorage.getId().toString());
                    assertThat(response.name()).isEqualTo("Keller");
                    assertThat(response.description()).isEqualTo("Lagerraum im Haus");
                });
    }

    @Test
    void updateStorage_shouldUpdateAndReturnStorage() {
        final Storage savedStorage = createStorage("Keller", "Lagerraum im Haus", null);

        StorageChildDto updateRequest = new StorageChildDto(savedStorage.getId().toString(), "Keller Updated", "Neue Beschreibung", null);

        webTestClient.post()
                .uri(addBaseUrl("/storages/update"))
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StorageChildDto.class)
                .value(response -> {
                    assertThat(response.name()).isEqualTo("Keller Updated");
                    assertThat(response.description()).isEqualTo("Neue Beschreibung");
                });
    }

    @Test
    void deleteStorage_shouldRemoveStorage() {
        final Storage savedStorage = createStorage("Keller", "Lagerraum im Haus", null);

        webTestClient.delete()
                .uri(addBaseUrl("/storages/delete/" + savedStorage.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(storageRepository.findById(savedStorage.getId())).isEmpty();
    }

    @Test
    void createStorage_shouldReturn401WhenNotAuthenticated() {
        StorageChildDto request = new StorageChildDto(null, "Keller", "Lagerraum im Haus", null);

        anonymousWebTestClient.post()
                .uri(addBaseUrl("/storages"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createStorage_shouldReturn400WhenNameIsBlank() {
        StorageChildDto request = new StorageChildDto(null, "", "Beschreibung", null);

        webTestClient.post()
                .uri(addBaseUrl("/storages"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void createStorage_withParent_shouldLinkChildToParent() {
        final Storage parent = createStorage("Keller", null, null);
        StorageChildDto request = new StorageChildDto(null, "Regal", "Metallregal", parent.getId().toString());

        webTestClient.post()
                .uri(addBaseUrl("/storages"))
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StorageChildDto.class)
                .value(response -> assertThat(response.parentId()).isEqualTo(parent.getId().toString()));

        assertThat(storageRepository.findById(parent.getId()).orElseThrow().getChildren())
                .extracting(Storage::getName)
                .containsExactly("Regal");
    }

    @Test
    void getAllStorages_shouldReturnOnlyRootStoragesWithChildren() {
        final Storage parent = createStorage("Keller", null, null);
        createStorage("Regal", null, parent);

        webTestClient.get()
                .uri(addBaseUrl("/storages"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StorageParentDto.class)
                .hasSize(1)
                .value(storages -> {
                    assertThat(storages.getFirst().name()).isEqualTo("Keller");
                    assertThat(storages.getFirst().children()).extracting(StorageParentDto::name)
                            .containsExactly("Regal");
                });
    }

    @Test
    void deleteStorage_shouldDeleteChildStorageOnFirstCall() {
        final Storage parent = createStorage("Keller", null, null);
        final Storage child = createStorage("Regal", null, parent);

        webTestClient.delete()
                .uri(addBaseUrl("/storages/delete/" + child.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(storageRepository.findById(child.getId())).isEmpty();
        assertThat(storageRepository.findById(parent.getId())).isPresent();
        assertThat(storageRepository.findById(parent.getId()).orElseThrow().getChildren()).isEmpty();
    }

    @Test
    void deleteStorage_shouldDeleteParentWithChildrenOnFirstCall() {
        final Storage parent = createStorage("Keller", null, null);
        final Storage child = createStorage("Regal", null, parent);
        final Storage grandChild = createStorage("Kiste", null, child);

        webTestClient.delete()
                .uri(addBaseUrl("/storages/delete/" + parent.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(storageRepository.findById(parent.getId())).isEmpty();
        assertThat(storageRepository.findById(child.getId())).isEmpty();
        assertThat(storageRepository.findById(grandChild.getId())).isEmpty();
    }

    private Storage createStorage(String name, String description, Storage parent) {
        final Storage storage = new Storage();
        storage.setName(name);
        storage.setDescription(description);
        storage.setUser(testUser);
        if (parent != null) {
            final Storage managedParent = storageRepository.findById(parent.getId()).orElseThrow();
            managedParent.addChild(storage);
        }
        return storageRepository.save(storage);
    }
}
