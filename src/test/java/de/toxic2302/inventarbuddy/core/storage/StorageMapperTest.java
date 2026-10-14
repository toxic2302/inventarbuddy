package de.toxic2302.inventarbuddy.core.storage;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.mapper.StorageMapper;
import de.toxic2302.inventarbuddy.core.modules.storage.mapper.StorageMapperImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StorageMapperTest {

    private StorageMapper storageMapper;

    @BeforeEach
    void setUp() {
        storageMapper = new StorageMapperImpl();
    }

    @Test
    void mapToDto_shouldMapAllFields() {
        final Storage storage = createStorage("Keller", "Lagerraum");

        final StorageParentDto result = storageMapper.mapToDto(storage);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(storage.getId().toString());
        assertThat(result.name()).isEqualTo("Keller");
        assertThat(result.description()).isEqualTo("Lagerraum");
        assertThat(result.children()).isEmpty();
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(storageMapper.mapToDto(null)).isNull();
    }

    @Test
    void mapToDto_shouldMapNestedChildrenRecursively() {
        final Storage parent = createStorage("Keller", null);
        final Storage child = createStorage("Regal", null);
        final Storage grandChild = createStorage("Kiste", null);
        parent.addChild(child);
        child.addChild(grandChild);

        final StorageParentDto result = storageMapper.mapToDto(parent);

        assertThat(result.children()).extracting(StorageParentDto::name).containsExactly("Regal");
        assertThat(result.children().getFirst().children()).extracting(StorageParentDto::name)
                .containsExactly("Kiste");
    }

    @Test
    void mapToDtoList_shouldMapAllEntities() {
        final List<StorageParentDto> result = storageMapper.mapToDtoList(
                List.of(createStorage("Keller", null), createStorage("Garage", null)));

        assertThat(result).extracting(StorageParentDto::name).containsExactly("Keller", "Garage");
    }

    @Test
    void mapToDtoList_shouldReturnEmptyListForEmptyInput() {
        assertThat(storageMapper.mapToDtoList(List.of())).isEmpty();
    }

    @Test
    void mapToDtoList_shouldReturnNullWhenInputIsNull() {
        assertThat(storageMapper.mapToDtoList(null)).isNull();
    }

    @Test
    void mapToChildDto_shouldSetParentIdWhenParentExists() {
        final Storage parent = createStorage("Keller", null);
        final Storage child = createStorage("Regal", "Metallregal");
        parent.addChild(child);

        final StorageChildDto result = storageMapper.mapToChildDto(child);

        assertThat(result.id()).isEqualTo(child.getId().toString());
        assertThat(result.name()).isEqualTo("Regal");
        assertThat(result.description()).isEqualTo("Metallregal");
        assertThat(result.parentId()).isEqualTo(parent.getId().toString());
    }

    @Test
    void mapToChildDto_shouldSetParentIdToNullWhenNoParent() {
        final StorageChildDto result = storageMapper.mapToChildDto(createStorage("Keller", null));

        assertThat(result.parentId()).isNull();
    }

    @Test
    void mapToChildDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(storageMapper.mapToChildDto(null)).isNull();
    }

    @Test
    void mapToChildDtoList_shouldMapAllEntities() {
        final Storage parent = createStorage("Keller", null);
        final Storage child = createStorage("Regal", null);
        parent.addChild(child);

        final List<StorageChildDto> result = storageMapper.mapToChildDtoList(List.of(parent, child));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).parentId()).isNull();
        assertThat(result.get(1).parentId()).isEqualTo(parent.getId().toString());
    }

    @Test
    void mapToEntity_shouldMapFieldsAndIgnoreParentAndChildren() {
        final UUID id = UUID.randomUUID();
        final StorageChildDto dto = new StorageChildDto(id.toString(), "Regal", "Metallregal",
                UUID.randomUUID().toString());

        final Storage result = storageMapper.mapToEntity(dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Regal");
        assertThat(result.getDescription()).isEqualTo("Metallregal");
        assertThat(result.getParent()).isNull();
        assertThat(result.getChildren()).isEmpty();
    }

    @Test
    void mapToEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(storageMapper.mapToEntity((StorageChildDto) null)).isNull();
    }

    private Storage createStorage(String name, String description) {
        final Storage storage = new Storage();
        storage.setId(UUID.randomUUID());
        storage.setName(name);
        storage.setDescription(description);
        return storage;
    }
}
