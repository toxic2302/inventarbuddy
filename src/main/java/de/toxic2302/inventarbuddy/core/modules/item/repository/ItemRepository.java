package de.toxic2302.inventarbuddy.core.modules.item.repository;

import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, UUID> {
  //List<Item> findAllByUserId(UUID user_id);
  List<Item> findAllByUserId(Long userId);
}
