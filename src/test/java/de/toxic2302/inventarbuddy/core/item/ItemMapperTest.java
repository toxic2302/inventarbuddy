package de.toxic2302.inventarbuddy.core.item;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.mapper.ItemMapper;
import de.toxic2302.inventarbuddy.core.modules.item.mapper.ItemMapperImpl;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.media.mapper.MediaMapperImpl;
import org.springframework.test.util.ReflectionTestUtils;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
        ReflectionTestUtils.setField(itemMapper, "mediaMapper", new MediaMapperImpl());
    }

    @Test
    void mapToDto_shouldMapListOfMedia() {
        final Item item = createItem();
        final Media pdf = new Media();
        pdf.setId(UUID.randomUUID());
        pdf.setFileName("manual.pdf");
        pdf.setContentType("application/pdf");
        pdf.setItem(item);
        final Media png = new Media();
        png.setId(UUID.randomUUID());
        png.setFileName("photo.png");
        png.setContentType("image/png");
        png.setItem(item);
        item.getMedia().add(pdf);
        item.getMedia().add(png);

        final ItemDto result = itemMapper.mapToDto(item);

        assertThat(result.media()).hasSize(2);
        assertThat(result.media()).extracting("fileName").containsExactly("manual.pdf", "photo.png");
        assertThat(result.media()).extracting("itemId").containsOnly(item.getId().toString());
    }

    @Test
    void mapToDto_shouldMapAllFields() {
        final Item item = createItem();

        final ItemDto result = itemMapper.mapToDto(item);

        assertThat(result.id()).isEqualTo(item.getId().toString());
        assertThat(result.name()).isEqualTo("Drill");
        assertThat(result.description()).isEqualTo("Cordless drill");
        assertThat(result.serialNumber()).isEqualTo("SN-1");
        assertThat(result.modelNumber()).isEqualTo("GSR 18V");
        assertThat(result.manufacturer()).isEqualTo("Bosch");
        assertThat(result.quantity()).isEqualTo(3);
        assertThat(result.sold()).isTrue();
        assertThat(result.categoryId()).isEqualTo(item.getCategory().getId().toString());
        assertThat(result.storageId()).isEqualTo(item.getStorage().getId().toString());
    }

    @Test
    void mapToDto_shouldHandleMissingCategoryAndStorage() {
        final Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setName("Drill");

        final ItemDto result = itemMapper.mapToDto(item);

        assertThat(result.categoryId()).isNull();
        assertThat(result.storageId()).isNull();
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(itemMapper.mapToDto(null)).isNull();
    }

    @Test
    void mapToEntity_shouldMapAllFields() {
        final UUID id = UUID.randomUUID();
        final ItemDto dto = new ItemDto(id.toString(), "Drill", "Cordless drill", "SN-1", "GSR 18V", "Bosch",
                3, true, UUID.randomUUID().toString(), UUID.randomUUID().toString(), null);

        final Item result = itemMapper.mapToEntity(dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getModelNumber()).isEqualTo("GSR 18V");
        assertThat(result.getManufacturer()).isEqualTo("Bosch");
        assertThat(result.getCategory()).isNull();
        assertThat(result.getStorage()).isNull();
        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.isSold()).isTrue();
    }

    @Test
    void mapToEntity_shouldKeepDefaultsWhenQuantityAndSoldAreMissing() {
        final ItemDto dto = new ItemDto(null, "Drill", null, null, null, null, null, null, null, null, null);

        final Item result = itemMapper.mapToEntity(dto);

        assertThat(result.getQuantity()).isEqualTo(1);
        assertThat(result.isSold()).isFalse();
    }

    @Test
    void updateEntity_shouldNotOverwriteIdAndUser() {
        final Item item = createItem();
        final UUID originalId = item.getId();
        final ItemDto dto = new ItemDto(UUID.randomUUID().toString(), "Saw", null, null, "S-1", "Makita", 5,
                false, null, null, null);

        itemMapper.updateEntity(dto, item);

        assertThat(item.getId()).isEqualTo(originalId);
        assertThat(item.getName()).isEqualTo("Saw");
        assertThat(item.getManufacturer()).isEqualTo("Makita");
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.isSold()).isFalse();
        assertThat(item.getCategory()).isNotNull();
        assertThat(item.getStorage()).isNotNull();
    }

    private Item createItem() {
        final Item item = new Item();
        item.setId(UUID.randomUUID());
        item.setName("Drill");
        item.setDescription("Cordless drill");
        item.setSerialNumber("SN-1");
        item.setModelNumber("GSR 18V");
        item.setManufacturer("Bosch");
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        item.setCategory(category);
        final Storage storage = new Storage();
        storage.setId(UUID.randomUUID());
        item.setStorage(storage);
        item.setQuantity(3);
        item.setSold(true);
        return item;
    }
}
