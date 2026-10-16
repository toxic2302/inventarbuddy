package de.toxic2302.inventarbuddy.core.modules.item.repository;

import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findAllByUser(User user);

    Optional<Item> findByIdAndUser(UUID id, User user);
}
