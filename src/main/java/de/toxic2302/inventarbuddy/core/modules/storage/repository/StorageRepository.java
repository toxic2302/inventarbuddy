package de.toxic2302.inventarbuddy.core.modules.storage.repository;

import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, UUID> {

    List<Storage> findAllByUser(User user);

    Optional<Storage> findByIdAndUser(UUID id, User user);

    List<Storage> findAllByUserAndParentIsNull(User user);
}
