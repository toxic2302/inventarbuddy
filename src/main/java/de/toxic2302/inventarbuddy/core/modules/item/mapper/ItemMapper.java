package de.toxic2302.inventarbuddy.core.modules.item.mapper;

import de.toxic2302.inventarbuddy.base.mapper.EntityMapper;
import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper extends EntityMapper<ItemDto, Item> {
}
