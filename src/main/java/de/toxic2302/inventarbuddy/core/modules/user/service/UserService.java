package de.toxic2302.inventarbuddy.core.modules.user.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticationService;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import de.toxic2302.inventarbuddy.core.modules.user.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  // ---- Globales ----
  private final UserRepository userRepository;
  private final AuthenticationService authenticationService;

  // ---- Constructor ----
  public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
    this.userRepository = userRepository;
    this.authenticationService = authenticationService;
  }

  // ---- Functions ----
  public User getCurrentUser() {
    return findByUsername(authenticationService.getUserName());
  }

  public User findByUsername(String username) {
    final Optional<User> existingUser = userRepository.findByUsername(username);

    return existingUser.orElseGet(() -> save(createUser()));
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  private User createUser() {
    return User.builder()
        .username(authenticationService.getUserName())
        .active(true)
        .email(authenticationService.getEmail())
        .firstName(authenticationService.getFirstName())
        .lastName(authenticationService.getLastName())
        .build();
  }
}
