package de.toxic2302.inventarbuddy.core.media;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.media.mapper.MediaMapper;
import de.toxic2302.inventarbuddy.core.modules.media.mapper.MediaMapperImpl;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaMapperTest {

    private MediaMapper mediaMapper;

    @BeforeEach
    void setUp() {
        mediaMapper = new MediaMapperImpl();
    }

    @Test
    void mapToDto_shouldMapAllFieldsWithoutData() {
        final Media media = new Media();
        media.setId(UUID.randomUUID());
        media.setFileName("manual.pdf");
        media.setContentType("application/pdf");
        media.setFileSize(3);
        media.setData(new byte[]{1, 2, 3});
        final Item item = new Item();
        item.setId(UUID.randomUUID());
        media.setItem(item);

        final MediaDto result = mediaMapper.mapToDto(media);

        assertThat(result.id()).isEqualTo(media.getId().toString());
        assertThat(result.fileName()).isEqualTo("manual.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(result.fileSize()).isEqualTo(3);
        assertThat(result.itemId()).isEqualTo(item.getId().toString());
    }

    @Test
    void mapToDto_shouldHandleMissingItem() {
        final Media media = new Media();
        media.setId(UUID.randomUUID());

        assertThat(mediaMapper.mapToDto(media).itemId()).isNull();
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(mediaMapper.mapToDto(null)).isNull();
    }

    @Test
    void mapToEntity_shouldNotMapDataItemAndUser() {
        final UUID id = UUID.randomUUID();
        final MediaDto dto = new MediaDto(id.toString(), "photo.png", "image/png", 5, UUID.randomUUID().toString());

        final Media result = mediaMapper.mapToEntity(dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getFileName()).isEqualTo("photo.png");
        assertThat(result.getContentType()).isEqualTo("image/png");
        assertThat(result.getData()).isNull();
        assertThat(result.getItem()).isNull();
        assertThat(result.getUser()).isNull();
    }
}
