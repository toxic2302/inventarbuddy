package de.toxic2302.inventarbuddy.core.modules.user.service;

import de.toxic2302.inventarbuddy.core.modules.user.dto.UserDto;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.mapper.UserMapper;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> listUsers() {
        return userMapper.mapToDtoList(userRepository.findAll());
    }

    public Optional<User> findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId);
    }

    public User saveUser(User newUser) {
        return userRepository.save(newUser);
    }

    public User updateUser(User updateUser) {
        return userRepository.save(updateUser);
    }

    public void deleteUser(String keycloakId) {
        findByKeycloakId(keycloakId).ifPresent(userRepository::delete);
    }
}
