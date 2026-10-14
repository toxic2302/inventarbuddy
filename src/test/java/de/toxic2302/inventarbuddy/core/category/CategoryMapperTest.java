package de.toxic2302.inventarbuddy.core.category;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryChildDto;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryParentDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.mapper.CategoryMapper;
import de.toxic2302.inventarbuddy.core.modules.category.mapper.CategoryMapperImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }

    @Test
    void mapToDto_shouldMapAllFields() {
        final Category category = createCategory("Elektronik", "Elektronische Geräte");

        final CategoryParentDto result = categoryMapper.mapToDto(category);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(category.getId().toString());
        assertThat(result.name()).isEqualTo("Elektronik");
        assertThat(result.description()).isEqualTo("Elektronische Geräte");
        assertThat(result.children()).isEmpty();
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(categoryMapper.mapToDto(null)).isNull();
    }

    @Test
    void mapToDto_shouldMapNestedChildrenRecursively() {
        final Category parent = createCategory("Elektronik", null);
        final Category child = createCategory("Handys", null);
        final Category grandChild = createCategory("Smartphones", null);
        parent.addChild(child);
        child.addChild(grandChild);

        final CategoryParentDto result = categoryMapper.mapToDto(parent);

        assertThat(result.children()).extracting(CategoryParentDto::name).containsExactly("Handys");
        assertThat(result.children().getFirst().children()).extracting(CategoryParentDto::name)
                .containsExactly("Smartphones");
    }

    @Test
    void mapToDtoList_shouldMapAllEntities() {
        final List<CategoryParentDto> result = categoryMapper.mapToDtoList(
                List.of(createCategory("Elektronik", null), createCategory("Möbel", null)));

        assertThat(result).extracting(CategoryParentDto::name).containsExactly("Elektronik", "Möbel");
    }

    @Test
    void mapToDtoList_shouldReturnEmptyListForEmptyInput() {
        assertThat(categoryMapper.mapToDtoList(List.of())).isEmpty();
    }

    @Test
    void mapToDtoList_shouldReturnNullWhenInputIsNull() {
        assertThat(categoryMapper.mapToDtoList(null)).isNull();
    }

    @Test
    void mapToChildDto_shouldSetParentIdWhenParentExists() {
        final Category parent = createCategory("Elektronik", null);
        final Category child = createCategory("Handys", "Mobiltelefone");
        parent.addChild(child);

        final CategoryChildDto result = categoryMapper.mapToChildDto(child);

        assertThat(result.id()).isEqualTo(child.getId().toString());
        assertThat(result.name()).isEqualTo("Handys");
        assertThat(result.description()).isEqualTo("Mobiltelefone");
        assertThat(result.parentId()).isEqualTo(parent.getId().toString());
    }

    @Test
    void mapToChildDto_shouldSetParentIdToNullWhenNoParent() {
        final CategoryChildDto result = categoryMapper.mapToChildDto(createCategory("Elektronik", null));

        assertThat(result.parentId()).isNull();
    }

    @Test
    void mapToChildDto_shouldReturnNullWhenEntityIsNull() {
        assertThat(categoryMapper.mapToChildDto(null)).isNull();
    }

    @Test
    void mapToChildDtoList_shouldMapAllEntities() {
        final Category parent = createCategory("Elektronik", null);
        final Category child = createCategory("Handys", null);
        parent.addChild(child);

        final List<CategoryChildDto> result = categoryMapper.mapToChildDtoList(List.of(parent, child));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).parentId()).isNull();
        assertThat(result.get(1).parentId()).isEqualTo(parent.getId().toString());
    }

    @Test
    void mapToEntity_shouldMapFieldsAndIgnoreParentAndChildren() {
        final UUID id = UUID.randomUUID();
        final CategoryChildDto dto = new CategoryChildDto(id.toString(), "Handys", "Mobiltelefone",
                UUID.randomUUID().toString());

        final Category result = categoryMapper.mapToEntity(dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Handys");
        assertThat(result.getDescription()).isEqualTo("Mobiltelefone");
        assertThat(result.getParent()).isNull();
        assertThat(result.getChildren()).isEmpty();
    }

    @Test
    void mapToEntity_shouldReturnNullWhenDtoIsNull() {
        assertThat(categoryMapper.mapToEntity((CategoryChildDto) null)).isNull();
    }

    private Category createCategory(String name, String description) {
        final Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}
