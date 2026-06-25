package de.toxic2302.inventarbuddy.core.modules.user.mapper;

import de.toxic2302.inventarbuddy.base.mapper.BaseMapper;
import de.toxic2302.inventarbuddy.base.mapper.BaseMapperConfig;
import de.toxic2302.inventarbuddy.core.modules.user.dto.UserDto;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface UserMapper extends BaseMapper<User, UserDto> {

    @Override
    @Mapping(target = "keycloakId", ignore = true)
    List<UserDto> mapToDtoList(List<User> entities);
}
