package de.toxic2302.inventarbuddy.core.modules.item.service;

import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.mapper.ItemMapper;
import de.toxic2302.inventarbuddy.core.modules.item.repository.ItemRepository;
import de.toxic2302.inventarbuddy.core.modules.user.service.UserService;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  // ---- Globales ----
  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;
  private final UserService userService;

  // ---- Constructor ----
  public ItemService(ItemRepository itemRepository, ItemMapper itemMapper, UserService userService) {
    this.itemRepository = itemRepository;
    this.itemMapper = itemMapper;
    this.userService = userService;
  }

  // ---- Functions ----
  public Collection<ItemDto> getAllItems() {
    return itemMapper.toDtoCollection(itemRepository.findAllByUser(userService.getCurrentUser()));
  }

  /*public Collection<ItemDto> getAllItemsByUser(String userid) {
    return itemMapper.toDtoCollection(itemRepository.findAllByUserId(Long.valueOf(userid)));
  }*/

  public Optional<ItemDto> getItemById(UUID itemId) {
    //TODO 26.11.24 floriankolb: eventuell anders machen
    final Optional<Item> itemById = itemRepository.findById(itemId);
    return itemMapper.toDtoOptional(itemById);
  }

  public ItemDto saveItem(ItemDto itemDto) {
    final Item entityToSave = itemMapper.toEntity(itemDto);
    entityToSave.setUser(userService.getCurrentUser());
    return itemMapper.toDto(itemRepository.save(entityToSave));
  }

  public ItemDto updateItem(ItemDto itemDto, UUID id) {
    final Optional<Item> existingItem = itemRepository.findById(id);

    existingItem.ifPresent(item -> {
      itemMapper.partialUpdate(item, itemDto);
    });

    itemRepository.save(existingItem.get());

    return itemMapper.toDto(existingItem.get());
  }

  public void deleteItemById(UUID itemId) {
    itemRepository.deleteById(itemId);
  }
}
