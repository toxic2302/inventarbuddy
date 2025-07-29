package de.toxic2302.inventarbuddy.core.modules.item.repository;

import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, UUID> {
  List<Item> findAllByUser(User user);
}
