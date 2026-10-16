package de.toxic2302.inventarbuddy.core.modules.media.repository;

import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.media.entity.Media;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    List<Media> findAllByUser(User user);

    List<Media> findAllByItemAndUser(Item item, User user);

    Optional<Media> findByIdAndUser(UUID id, User user);
}
