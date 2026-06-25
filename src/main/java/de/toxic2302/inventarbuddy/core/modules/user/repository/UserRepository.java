package de.toxic2302.inventarbuddy.core.modules.user.repository;

import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(String keycloakId);
}
