package de.toxic2302.inventarbuddy.core.modules.item.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.item.mapper.ItemMapper;
import de.toxic2302.inventarbuddy.core.modules.item.repository.ItemRepository;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.repository.StorageRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final StorageRepository storageRepository;
    private final ItemMapper itemMapper;

    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public ItemDto getItem(UUID id) {
        return itemMapper.mapToDto(findOwnedItem(id));
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllItemsByUser() {
        return itemMapper.mapToDtoList(itemRepository.findAllByUser(authenticatedUserService.getCurrentUser()));
    }

    @Transactional
    public ItemDto createItem(ItemDto itemDto) {
        final Item newItem = itemMapper.mapToEntity(itemDto);
        newItem.setId(null);
        newItem.setUser(authenticatedUserService.getCurrentUser());
        newItem.setCategory(findOwnedCategory(itemDto.categoryId()));
        newItem.setStorage(findOwnedStorage(itemDto.storageId()));

        return itemMapper.mapToDto(itemRepository.save(newItem));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto) {
        final Item existingItem = findOwnedItem(UUID.fromString(itemDto.id()));
        itemMapper.updateEntity(itemDto, existingItem);
        existingItem.setCategory(findOwnedCategory(itemDto.categoryId()));
        existingItem.setStorage(findOwnedStorage(itemDto.storageId()));

        return itemMapper.mapToDto(itemRepository.save(existingItem));
    }

    @Transactional
    public void deleteItem(UUID id) {
        itemRepository.delete(findOwnedItem(id));
    }

    private Category findOwnedCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return null;
        }
        return categoryRepository.findByIdAndUser(UUID.fromString(categoryId), authenticatedUserService.getCurrentUser())
                .orElseThrow();
    }

    private Storage findOwnedStorage(String storageId) {
        if (storageId == null || storageId.isBlank()) {
            return null;
        }
        return storageRepository.findByIdAndUser(UUID.fromString(storageId), authenticatedUserService.getCurrentUser())
                .orElseThrow();
    }

    private Item findOwnedItem(UUID id) {
        return itemRepository.findByIdAndUser(id, authenticatedUserService.getCurrentUser()).orElseThrow();
    }
}
