package de.toxic2302.inventarbuddy.core.storage;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.repository.StorageRepository;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
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
    void deleteStorage_shouldDeleteChildStorageOnFirstCall() {
        final Storage parent = createStorage("Keller", null);
        final Storage child = createStorage("Regal", parent);

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
        final Storage parent = createStorage("Keller", null);
        final Storage child = createStorage("Regal", parent);
        final Storage grandChild = createStorage("Kiste", child);

        webTestClient.delete()
                .uri(addBaseUrl("/storages/delete/" + parent.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(storageRepository.findById(parent.getId())).isEmpty();
        assertThat(storageRepository.findById(child.getId())).isEmpty();
        assertThat(storageRepository.findById(grandChild.getId())).isEmpty();
    }

    private Storage createStorage(String name, Storage parent) {
        final Storage storage = new Storage();
        storage.setName(name);
        storage.setUser(testUser);
        if (parent != null) {
            final Storage managedParent = storageRepository.findById(parent.getId()).orElseThrow();
            managedParent.addChild(storage);
        }
        return storageRepository.save(storage);
    }
}
