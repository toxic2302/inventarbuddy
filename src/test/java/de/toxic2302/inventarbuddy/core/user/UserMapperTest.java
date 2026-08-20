package de.toxic2302.inventarbuddy.core.user;

import static org.assertj.core.api.Assertions.assertThat;

import de.toxic2302.inventarbuddy.core.modules.user.dto.UserDto;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.mapper.UserMapper;
import de.toxic2302.inventarbuddy.core.modules.user.mapper.UserMapperImpl;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    void mapToDto_shouldMapAllFields() {
        User user = createUser("keycloak-1", "test@example.com", "Max", "Mustermann");

        UserDto result = userMapper.mapToDto(user);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.firstName()).isEqualTo("Max");
        assertThat(result.lastName()).isEqualTo("Mustermann");
    }

    @Test
    void mapToDto_shouldReturnNullWhenEntityIsNull() {
        UserDto result = userMapper.mapToDto(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToEntity_shouldMapAllFields() {
        UserDto dto = new UserDto("test@example.com", "Max", "Mustermann");

        User result = userMapper.mapToEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("Max");
        assertThat(result.getLastName()).isEqualTo("Mustermann");
        assertThat(result.getKeycloakId()).isNull();
    }

    @Test
    void mapToEntity_shouldReturnNullWhenDtoIsNull() {
        User result = userMapper.mapToEntity(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToDtoList_shouldMapAllEntities() {
        User user1 = createUser("kc-1", "a@example.com", "Anna", "Schmidt");
        User user2 = createUser("kc-2", "b@example.com", "Ben", "Müller");

        List<UserDto> result = userMapper.mapToDtoList(List.of(user1, user2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo("a@example.com");
        assertThat(result.get(0).firstName()).isEqualTo("Anna");
        assertThat(result.get(1).email()).isEqualTo("b@example.com");
        assertThat(result.get(1).firstName()).isEqualTo("Ben");
    }

    @Test
    void mapToDtoList_shouldReturnEmptyListForEmptyInput() {
        List<UserDto> result = userMapper.mapToDtoList(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void mapToDtoList_shouldReturnNullWhenInputIsNull() {
        List<UserDto> result = userMapper.mapToDtoList(null);

        assertThat(result).isNull();
    }

    @Test
    void mapToEntity_shouldNotMapKeycloakId() {
        UserDto dto = new UserDto("test@example.com", "Max", "Mustermann");

        User result = userMapper.mapToEntity(dto);

        assertThat(result.getKeycloakId()).isNull();
    }

    private User createUser(String keycloakId, String email, String firstName, String lastName) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
