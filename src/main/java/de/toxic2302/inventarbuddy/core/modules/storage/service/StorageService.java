package de.toxic2302.inventarbuddy.core.modules.storage.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.mapper.StorageMapper;
import de.toxic2302.inventarbuddy.core.modules.storage.repository.StorageRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    private final AuthenticatedUserService authenticatedUserService;

    public StorageParentDto getStorage(UUID id) {
        return storageMapper.mapToDto(storageRepository.findById(id).orElseThrow());
    }

    public List<StorageParentDto> getAllStoragesByUser() {
        return storageMapper.mapToDtoList(
                storageRepository.findAllByUserAndParentIsNull(authenticatedUserService.getCurrentUser()));
    }

    @Transactional
    public StorageChildDto createStorage(StorageChildDto storageDto) {
        final Storage newStorage = storageMapper.mapToEntity(storageDto);
        newStorage.setUser(authenticatedUserService.getCurrentUser());

        if (storageDto.parentId() != null) {
            final Storage parent = storageRepository.findById(UUID.fromString(storageDto.parentId())).orElseThrow();
            parent.addChild(newStorage);
        }

        return storageMapper.mapToChildDto(storageRepository.save(newStorage));
    }

    @Transactional
    public StorageChildDto updateStorage(StorageChildDto storageDto) {
        final Storage storage = storageMapper.mapToEntity(storageDto);
        final Storage existingStorage = storageRepository.findById(storage.getId()).orElseThrow();

        final Storage oldParent = existingStorage.getParent();
        if (oldParent != null) {
            oldParent.removeChild(existingStorage);
        }

        if (storageDto.parentId() != null) {
            final Storage parent = storageRepository.findById(UUID.fromString(storageDto.parentId())).orElseThrow();
            parent.addChild(storage);
        }

        return storageMapper.mapToChildDto(storageRepository.save(storage));
    }

    @Transactional
    public void deleteStorage(UUID id) {
        final Storage storage = storageRepository.findById(id).orElseThrow();

        final Storage parent = storage.getParent();
        if (parent != null) {
            parent.removeChild(storage);
        }

        storageRepository.delete(storage);
    }
}
