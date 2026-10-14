package de.toxic2302.inventarbuddy.core.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
import de.toxic2302.inventarbuddy.core.modules.storage.entity.Storage;
import de.toxic2302.inventarbuddy.core.modules.storage.mapper.StorageMapper;
import de.toxic2302.inventarbuddy.core.modules.storage.repository.StorageRepository;
import de.toxic2302.inventarbuddy.core.modules.storage.service.StorageService;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private StorageMapper storageMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private StorageService storageService;

    private UUID storageId;
    private UUID parentId;
    private Storage storage;
    private StorageParentDto storageParentDto;
    private User user;

    @BeforeEach
    void setUp() {
        storageId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        user = new User();
        user.setKeycloakId("keycloak-123");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        storage = new Storage();
        storage.setId(storageId);
        storage.setName("Keller");
        storage.setDescription("Kellerraum");
        storage.setUser(user);

        storageParentDto = new StorageParentDto(storageId.toString(), "Keller", "Kellerraum", List.of());
    }

    @Test
    void getStorage_shouldReturnStorageDto() {
        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storage));
        when(storageMapper.mapToDto(storage)).thenReturn(storageParentDto);

        StorageParentDto result = storageService.getStorage(storageId);

        assertThat(result).isEqualTo(storageParentDto);
        verify(storageRepository).findById(storageId);
        verify(storageMapper).mapToDto(storage);
    }

    @Test
    void getStorage_shouldThrowWhenNotFound() {
        when(storageRepository.findById(storageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storageService.getStorage(storageId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllStoragesByUser_shouldReturnOnlyRootStorages() {
        List<Storage> storages = List.of(storage);
        List<StorageParentDto> storageDtos = List.of(storageParentDto);

        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(storageRepository.findAllByUserAndParentIsNull(user)).thenReturn(storages);
        when(storageMapper.mapToDtoList(storages)).thenReturn(storageDtos);

        List<StorageParentDto> result = storageService.getAllStoragesByUser();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(storageParentDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(storageRepository).findAllByUserAndParentIsNull(user);
        verify(storageMapper).mapToDtoList(storages);
    }

    @Test
    void createStorage_withoutParent_shouldCreateAndReturnStorage() {
        StorageChildDto storageDto = new StorageChildDto(null, "Keller", "Kellerraum", null);

        Storage newStorage = new Storage();
        newStorage.setName("Keller");
        newStorage.setDescription("Kellerraum");

        StorageChildDto resultDto = new StorageChildDto(storageId.toString(), "Keller", "Kellerraum", null);

        when(storageMapper.mapToEntity(storageDto)).thenReturn(newStorage);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(storageRepository.save(newStorage)).thenReturn(storage);
        when(storageMapper.mapToChildDto(storage)).thenReturn(resultDto);

        StorageChildDto result = storageService.createStorage(storageDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(newStorage.getUser()).isEqualTo(user);
        verify(storageMapper).mapToEntity(storageDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(storageRepository).save(newStorage);
        verify(storageMapper).mapToChildDto(storage);
    }

    @Test
    void createStorage_withParent_shouldLinkToParent() {
        StorageChildDto storageDto = new StorageChildDto(null, "Regal", "Regal im Keller", parentId.toString());

        Storage parent = new Storage();
        parent.setId(parentId);
        parent.setName("Keller");

        Storage newStorage = new Storage();
        newStorage.setName("Regal");
        newStorage.setDescription("Regal im Keller");

        StorageChildDto resultDto = new StorageChildDto(storageId.toString(), "Regal", "Regal im Keller", parentId.toString());

        when(storageMapper.mapToEntity(storageDto)).thenReturn(newStorage);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(storageRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(storageRepository.save(newStorage)).thenReturn(storage);
        when(storageMapper.mapToChildDto(storage)).thenReturn(resultDto);

        StorageChildDto result = storageService.createStorage(storageDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(newStorage.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(newStorage);
        verify(storageRepository).findById(parentId);
        verify(storageRepository).save(newStorage);
    }

    @Test
    void createStorage_withUnknownParent_shouldThrow() {
        StorageChildDto storageDto = new StorageChildDto(null, "Regal", "Regal im Keller", parentId.toString());

        Storage newStorage = new Storage();
        newStorage.setName("Regal");

        when(storageMapper.mapToEntity(storageDto)).thenReturn(newStorage);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(storageRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storageService.createStorage(storageDto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateStorage_withParent_shouldSetParent() {
        StorageChildDto storageDto = new StorageChildDto(storageId.toString(), "Keller Updated", "Kellerraum", parentId.toString());

        Storage parent = new Storage();
        parent.setId(parentId);

        Storage updatedStorage = new Storage();
        updatedStorage.setId(storageId);
        updatedStorage.setName("Keller Updated");

        Storage existingStorage = new Storage();
        existingStorage.setId(storageId);

        StorageChildDto resultDto = new StorageChildDto(storageId.toString(), "Keller Updated", "Kellerraum", parentId.toString());

        when(storageMapper.mapToEntity(storageDto)).thenReturn(updatedStorage);
        when(storageRepository.findById(storageId)).thenReturn(Optional.of(existingStorage));
        when(storageRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(storageRepository.save(updatedStorage)).thenReturn(updatedStorage);
        when(storageMapper.mapToChildDto(updatedStorage)).thenReturn(resultDto);

        StorageChildDto result = storageService.updateStorage(storageDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(updatedStorage.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(updatedStorage);
        verify(storageRepository).findById(storageId);
        verify(storageRepository).findById(parentId);
        verify(storageRepository).save(updatedStorage);
    }

    @Test
    void updateStorage_withExistingParent_shouldRemoveFromOldParent() {
        StorageChildDto storageDto = new StorageChildDto(storageId.toString(), "Regal Updated", "Regal", null);

        Storage oldParent = new Storage();
        oldParent.setId(UUID.randomUUID());

        Storage existingStorage = new Storage();
        existingStorage.setId(storageId);
        oldParent.addChild(existingStorage);

        Storage updatedStorage = new Storage();
        updatedStorage.setId(storageId);
        updatedStorage.setName("Regal Updated");

        StorageChildDto resultDto = new StorageChildDto(storageId.toString(), "Regal Updated", "Regal", null);

        when(storageMapper.mapToEntity(storageDto)).thenReturn(updatedStorage);
        when(storageRepository.findById(storageId)).thenReturn(Optional.of(existingStorage));
        when(storageRepository.save(updatedStorage)).thenReturn(updatedStorage);
        when(storageMapper.mapToChildDto(updatedStorage)).thenReturn(resultDto);

        StorageChildDto result = storageService.updateStorage(storageDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(oldParent.getChildren()).doesNotContain(existingStorage);
        assertThat(existingStorage.getParent()).isNull();
        verify(storageRepository).findById(storageId);
        verify(storageRepository).save(updatedStorage);
    }

    @Test
    void deleteStorage_withoutParent_shouldDeleteById() {
        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storage));

        storageService.deleteStorage(storageId);

        verify(storageRepository).findById(storageId);
        verify(storageRepository).delete(storage);
    }

    @Test
    void deleteStorage_withParent_shouldRemoveFromParentBeforeDelete() {
        Storage parent = new Storage();
        parent.setId(parentId);
        parent.addChild(storage);

        when(storageRepository.findById(storageId)).thenReturn(Optional.of(storage));

        storageService.deleteStorage(storageId);

        assertThat(parent.getChildren()).doesNotContain(storage);
        assertThat(storage.getParent()).isNull();
        verify(storageRepository).findById(storageId);
        verify(storageRepository).delete(storage);
    }

    @Test
    void deleteStorage_shouldThrowWhenNotFound() {
        when(storageRepository.findById(storageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storageService.deleteStorage(storageId))
                .isInstanceOf(NoSuchElementException.class);
    }
}
