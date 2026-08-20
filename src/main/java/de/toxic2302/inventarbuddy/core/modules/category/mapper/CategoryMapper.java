package de.toxic2302.inventarbuddy.core.modules.category.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface CategoryMapper extends BaseMapper<Category, CategoryDto> {
}
