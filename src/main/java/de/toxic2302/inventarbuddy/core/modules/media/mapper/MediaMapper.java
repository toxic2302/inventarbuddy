package de.toxic2302.inventarbuddy.core.modules.media.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface MediaMapper extends BaseMapper<Media, MediaDto> {

    @Override
    @Mapping(target = "itemId", source = "item.id")
    MediaDto mapToDto(Media entity);

    @Override
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "user", ignore = true)
    Media mapToEntity(MediaDto dto);
}
