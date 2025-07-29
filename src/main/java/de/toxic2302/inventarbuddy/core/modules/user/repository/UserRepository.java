package de.toxic2302.inventarbuddy.core.modules.user.repository;

import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

}
