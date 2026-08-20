package de.toxic2302.inventarbuddy.core.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.mapper.CategoryMapper;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import de.toxic2302.inventarbuddy.core.modules.category.service.CategoryService;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private Category category;
    private CategoryDto categoryDto;
    private User user;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        user = new User();
        user.setKeycloakId("keycloak-123");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        category = new Category();
        category.setId(categoryId);
        category.setName("Elektronik");
        category.setDescription("Elektronische Geräte");
        category.setUser(user);

        categoryDto = new CategoryDto(categoryId.toString(), "Elektronik", "Elektronische Geräte");
    }

    @Test
    void getCategory_shouldReturnCategoryDto() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.getCategory(categoryId);

        assertThat(result).isEqualTo(categoryDto);
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).mapToDto(category);
    }

    @Test
    void getCategory_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategory(categoryId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllCategoriesByUser_shouldReturnCategoryList() {
        List<Category> categories = List.of(category);
        List<CategoryDto> categoryDtos = List.of(categoryDto);

        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAllByUser(user)).thenReturn(categories);
        when(categoryMapper.mapToDtoList(categories)).thenReturn(categoryDtos);

        List<CategoryDto> result = categoryService.getAllCategoriesByUser();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(categoryDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(categoryRepository).findAllByUser(user);
        verify(categoryMapper).mapToDtoList(categories);
    }

    @Test
    void getAllCategoriesByUser_shouldReturnEmptyListWhenNoCategories() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAllByUser(user)).thenReturn(List.of());
        when(categoryMapper.mapToDtoList(List.of())).thenReturn(List.of());

        List<CategoryDto> result = categoryService.getAllCategoriesByUser();

        assertThat(result).isEmpty();
    }

    @Test
    void createCategory_shouldCreateAndReturnCategory() {
        Category newCategory = new Category();
        newCategory.setName("Elektronik");
        newCategory.setDescription("Elektronische Geräte");

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(newCategory);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.save(newCategory)).thenReturn(category);
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDto);

        CategoryDto result = categoryService.createCategory(categoryDto);

        assertThat(result).isEqualTo(categoryDto);
        assertThat(newCategory.getUser()).isEqualTo(user);
        verify(categoryMapper).mapToEntity(categoryDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(categoryRepository).save(newCategory);
        verify(categoryMapper).mapToDto(category);
    }

    @Test
    void updateCategory_shouldUpdateAndReturnCategory() {
        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Elektronik Updated");

        CategoryDto updatedDto = new CategoryDto(categoryId.toString(), "Elektronik Updated", "Elektronische Geräte");

        when(categoryMapper.mapToEntity(updatedDto)).thenReturn(updatedCategory);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.mapToDto(updatedCategory)).thenReturn(updatedDto);

        CategoryDto result = categoryService.updateCategory(updatedDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(categoryMapper).mapToEntity(updatedDto);
        verify(categoryRepository).save(updatedCategory);
        verify(categoryMapper).mapToDto(updatedCategory);
    }

    @Test
    void deleteCategory_shouldDeleteById() {
        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }
}
