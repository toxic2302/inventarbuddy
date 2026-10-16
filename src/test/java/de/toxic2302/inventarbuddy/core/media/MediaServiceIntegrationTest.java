package de.toxic2302.inventarbuddy.core.media;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.base.BaseIntegrationTest;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.repository.ItemRepository;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.media.repository.MediaRepository;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MediaServiceIntegrationTest extends BaseIntegrationTest {

    private static final byte[] PDF = {'%', 'P', 'D', 'F', 1, 2, 3};
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 1, 2, 3};

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        clean();

        testUser = new User();
        testUser.setKeycloakId("test-user");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        clean();
    }

    private void clean() {
        mediaRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createMedia_shouldStorePdfInDatabaseAndLinkItem() {
        final Item item = createItem("Drill", testUser);

        webTestClient.post()
                .uri(addBaseUrl("/media?itemId=" + item.getId()))
                .body(BodyInserters.fromMultipartData(filePart(PDF, "manual.pdf", MediaType.APPLICATION_PDF)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MediaDto.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.fileName()).isEqualTo("manual.pdf");
                    assertThat(response.contentType()).isEqualTo("application/pdf");
                    assertThat(response.fileSize()).isEqualTo(PDF.length);
                    assertThat(response.itemId()).isEqualTo(item.getId().toString());
                });

        final Media stored = mediaRepository.findAll().getFirst();
        assertThat(stored.getData()).isEqualTo(PDF);
        assertThat(stored.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(stored.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createMedia_shouldStoreImageWithoutItem() {
        webTestClient.post()
                .uri(addBaseUrl("/media"))
                .body(BodyInserters.fromMultipartData(filePart(PNG, "photo.png", MediaType.IMAGE_PNG)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MediaDto.class)
                .value(response -> {
                    assertThat(response.contentType()).isEqualTo("image/png");
                    assertThat(response.itemId()).isNull();
                });
    }

    @Test
    void createMedia_shouldRejectUnsupportedFileType() {
        webTestClient.post()
                .uri(addBaseUrl("/media"))
                .body(BodyInserters.fromMultipartData(filePart(new byte[]{1, 2}, "notes.txt", MediaType.TEXT_PLAIN)))
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(mediaRepository.findAll()).isEmpty();
    }

    @Test
    void createMedia_shouldRejectEmptyFile() {
        webTestClient.post()
                .uri(addBaseUrl("/media"))
                .body(BodyInserters.fromMultipartData(filePart(new byte[0], "empty.pdf", MediaType.APPLICATION_PDF)))
                .exchange()
                .expectStatus().is4xxClientError();

        assertThat(mediaRepository.findAll()).isEmpty();
    }

    @Test
    void createMedia_shouldRejectItemOfOtherUser() {
        final Item foreign = createItem("Foreign", createOtherUser());

        webTestClient.post()
                .uri(addBaseUrl("/media?itemId=" + foreign.getId()))
                .body(BodyInserters.fromMultipartData(filePart(PDF, "manual.pdf", MediaType.APPLICATION_PDF)))
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(mediaRepository.findAll()).isEmpty();
    }

    @Test
    void createMedia_shouldReturn401WhenNotAuthenticated() {
        anonymousWebTestClient.post()
                .uri(addBaseUrl("/media"))
                .body(BodyInserters.fromMultipartData(filePart(PDF, "manual.pdf", MediaType.APPLICATION_PDF)))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getMediaContent_shouldReturnBytesWithStoredContentType() {
        final Media media = createMedia("manual.pdf", "application/pdf", PDF, null, testUser);

        webTestClient.get()
                .uri(addBaseUrl("/media/" + media.getId() + "/content"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectBody(byte[].class)
                .isEqualTo(PDF);
    }

    @Test
    void getMediaById_shouldReturnMetadata() {
        final Media media = createMedia("photo.png", "image/png", PNG, null, testUser);

        webTestClient.get()
                .uri(addBaseUrl("/media/" + media.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MediaDto.class)
                .value(response -> {
                    assertThat(response.fileName()).isEqualTo("photo.png");
                    assertThat(response.fileSize()).isEqualTo(PNG.length);
                });
    }

    @Test
    void getMedia_shouldNotBeAccessibleForOtherUser() {
        final Media foreign = createMedia("secret.pdf", "application/pdf", PDF, null, createOtherUser());

        webTestClient.get()
                .uri(addBaseUrl("/media/" + foreign.getId()))
                .exchange()
                .expectStatus().is5xxServerError();

        webTestClient.get()
                .uri(addBaseUrl("/media/" + foreign.getId() + "/content"))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getAllMedia_shouldReturnOnlyMediaOfAuthenticatedUser() {
        createMedia("a.pdf", "application/pdf", PDF, null, testUser);
        createMedia("b.png", "image/png", PNG, null, testUser);
        createMedia("c.pdf", "application/pdf", PDF, null, createOtherUser());

        webTestClient.get()
                .uri(addBaseUrl("/media"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MediaDto.class)
                .value(list -> assertThat(list).extracting(MediaDto::fileName)
                        .containsExactlyInAnyOrder("a.pdf", "b.png"));
    }

    @Test
    void getAllMediaByItem_shouldReturnOnlyMediaOfThatItem() {
        final Item drill = createItem("Drill", testUser);
        final Item saw = createItem("Saw", testUser);
        createMedia("drill.pdf", "application/pdf", PDF, drill, testUser);
        createMedia("saw.png", "image/png", PNG, saw, testUser);

        webTestClient.get()
                .uri(addBaseUrl("/media/item/" + drill.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MediaDto.class)
                .value(list -> assertThat(list).extracting(MediaDto::fileName).containsExactly("drill.pdf"));
    }

    @Test
    void updateMedia_shouldRenameAndRelinkItem() {
        final Item item = createItem("Drill", testUser);
        final Media media = createMedia("old.pdf", "application/pdf", PDF, null, testUser);

        webTestClient.post()
                .uri(addBaseUrl("/media/update"))
                .bodyValue(new MediaDto(media.getId().toString(), "new.pdf", null, 0, item.getId().toString()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MediaDto.class)
                .value(response -> {
                    assertThat(response.fileName()).isEqualTo("new.pdf");
                    assertThat(response.itemId()).isEqualTo(item.getId().toString());
                });

        final Media updated = mediaRepository.findById(media.getId()).orElseThrow();
        assertThat(updated.getData()).isEqualTo(PDF);
        assertThat(updated.getContentType()).isEqualTo("application/pdf");

        webTestClient.post()
                .uri(addBaseUrl("/media/update"))
                .bodyValue(new MediaDto(media.getId().toString(), "new.pdf", null, 0, null))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MediaDto.class)
                .value(response -> assertThat(response.itemId()).isNull());
    }

    @Test
    void deleteMedia_shouldRemoveMedia() {
        final Media media = createMedia("a.pdf", "application/pdf", PDF, null, testUser);

        webTestClient.delete()
                .uri(addBaseUrl("/media/delete/" + media.getId()))
                .exchange()
                .expectStatus().isOk();

        assertThat(mediaRepository.findById(media.getId())).isEmpty();
    }

    @Test
    void deleteMedia_shouldNotDeleteMediaOfOtherUser() {
        final Media foreign = createMedia("a.pdf", "application/pdf", PDF, null, createOtherUser());

        webTestClient.delete()
                .uri(addBaseUrl("/media/delete/" + foreign.getId()))
                .exchange()
                .expectStatus().is5xxServerError();

        assertThat(mediaRepository.findById(foreign.getId())).isPresent();
    }

    private MultiValueMap<String, HttpEntity<?>> filePart(byte[] content, String fileName, MediaType mediaType) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return fileName;
            }
        }).contentType(mediaType);
        return builder.build();
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
        item.setUser(owner);
        return itemRepository.save(item);
    }

    private Media createMedia(String fileName, String contentType, byte[] data, Item item, User owner) {
        final Media media = new Media();
        media.setFileName(fileName);
        media.setContentType(contentType);
        media.setFileSize(data.length);
        media.setData(data);
        media.setItem(item);
        media.setUser(owner);
        return mediaRepository.save(media);
    }
}
