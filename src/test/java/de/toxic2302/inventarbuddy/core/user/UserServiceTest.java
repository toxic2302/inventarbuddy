package de.toxic2302.inventarbuddy.core.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.toxic2302.inventarbuddy.core.modules.user.dto.UserDto;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.mapper.UserMapper;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
import de.toxic2302.inventarbuddy.core.modules.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setKeycloakId("keycloak-123");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        userDto = new UserDto("test@example.com", "Test", "User");
    }

    @Test
    void listUsers_shouldReturnUserDtoList() {
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.mapToDtoList(users)).thenReturn(userDtos);

        List<UserDto> result = userService.listUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(userDto);
        verify(userRepository).findAll();
        verify(userMapper).mapToDtoList(users);
    }

    @Test
    void listUsers_shouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.mapToDtoList(List.of())).thenReturn(List.of());

        List<UserDto> result = userService.listUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void findByKeycloakId_shouldReturnUserWhenFound() {
        when(userRepository.findByKeycloakId("keycloak-123")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByKeycloakId("keycloak-123");

        assertThat(result).isPresent();
        assertThat(result.get().getKeycloakId()).isEqualTo("keycloak-123");
        verify(userRepository).findByKeycloakId("keycloak-123");
    }

    @Test
    void findByKeycloakId_shouldReturnEmptyWhenNotFound() {
        when(userRepository.findByKeycloakId("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByKeycloakId("unknown");

        assertThat(result).isEmpty();
        verify(userRepository).findByKeycloakId("unknown");
    }

    @Test
    void saveUser_shouldPersistAndReturnUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_shouldSaveAndReturnUser() {
        user.setEmail("updated@example.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldDeleteWhenUserExists() {
        when(userRepository.findByKeycloakId("keycloak-123")).thenReturn(Optional.of(user));

        userService.deleteUser("keycloak-123");

        verify(userRepository).findByKeycloakId("keycloak-123");
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldDoNothingWhenUserNotFound() {
        when(userRepository.findByKeycloakId("unknown")).thenReturn(Optional.empty());

        userService.deleteUser("unknown");

        verify(userRepository).findByKeycloakId("unknown");
    }
}
