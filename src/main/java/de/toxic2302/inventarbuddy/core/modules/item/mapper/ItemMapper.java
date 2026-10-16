package de.toxic2302.inventarbuddy.core.modules.item.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.media.mapper.MediaMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class, uses = MediaMapper.class)
public interface ItemMapper extends BaseMapper<Item, ItemDto> {

    @Override
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "storageId", source = "storage.id")
    ItemDto mapToDto(Item entity);

    @Override
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "media", ignore = true)
    Item mapToEntity(ItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "storage", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "media", ignore = true)
    void updateEntity(ItemDto dto, @MappingTarget Item entity);
}
