package de.toxic2302.inventarbuddy.core.modules.category.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryChildDto;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryParentDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface CategoryMapper extends BaseMapper<Category, CategoryParentDto> {

    @Mapping(target = "parentId", expression = "java(entity.getParent() != null ? entity.getParent().getId().toString() : null)")
    CategoryChildDto mapToChildDto(Category entity);

    List<CategoryChildDto> mapToChildDtoList(List<Category> entities);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    Category mapToEntity(CategoryChildDto dto);
}
