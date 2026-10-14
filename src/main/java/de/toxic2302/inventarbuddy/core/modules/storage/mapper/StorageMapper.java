package de.toxic2302.inventarbuddy.core.modules.storage.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface StorageMapper extends BaseMapper<Storage, StorageParentDto> {

    @Mapping(target = "parentId", expression = "java(entity.getParent() != null ? entity.getParent().getId().toString() : null)")
    StorageChildDto mapToChildDto(Storage entity);

    List<StorageChildDto> mapToChildDtoList(List<Storage> entities);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    Storage mapToEntity(StorageChildDto dto);
}
