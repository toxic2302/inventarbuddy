package de.toxic2302.inventarbuddy.core.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryChildDto;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryParentDto;
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
    private UUID parentId;
    private Category category;
    private CategoryParentDto categoryParentDto;
    private User user;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        parentId = UUID.randomUUID();

        user = new User();
        user.setKeycloakId("keycloak-123");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        category = new Category();
        category.setId(categoryId);
        category.setName("Keller");
        category.setDescription("Kellerraum");
        category.setUser(user);

        categoryParentDto = new CategoryParentDto(categoryId.toString(), "Keller", "Kellerraum", List.of());
    }

    @Test
    void getCategory_shouldReturnCategoryDto() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryParentDto);

        CategoryParentDto result = categoryService.getCategory(categoryId);

        assertThat(result).isEqualTo(categoryParentDto);
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
    void getAllCategoriesByUser_shouldReturnOnlyRootCategories() {
        List<Category> categories = List.of(category);
        List<CategoryParentDto> categoryDtos = List.of(categoryParentDto);

        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findAllByUserAndParentIsNull(user)).thenReturn(categories);
        when(categoryMapper.mapToDtoList(categories)).thenReturn(categoryDtos);

        List<CategoryParentDto> result = categoryService.getAllCategoriesByUser();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(categoryParentDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(categoryRepository).findAllByUserAndParentIsNull(user);
        verify(categoryMapper).mapToDtoList(categories);
    }

    @Test
    void createCategory_withoutParent_shouldCreateAndReturnCategory() {
        CategoryChildDto categoryDto = new CategoryChildDto(null, "Keller", "Kellerraum", null);

        Category newCategory = new Category();
        newCategory.setName("Keller");
        newCategory.setDescription("Kellerraum");

        CategoryChildDto resultDto = new CategoryChildDto(categoryId.toString(), "Keller", "Kellerraum", null);

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(newCategory);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.save(newCategory)).thenReturn(category);
        when(categoryMapper.mapToChildDto(category)).thenReturn(resultDto);

        CategoryChildDto result = categoryService.createCategory(categoryDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(newCategory.getUser()).isEqualTo(user);
        verify(categoryMapper).mapToEntity(categoryDto);
        verify(authenticatedUserService).getCurrentUser();
        verify(categoryRepository).save(newCategory);
        verify(categoryMapper).mapToChildDto(category);
    }

    @Test
    void createCategory_withParent_shouldLinkToParent() {
        CategoryChildDto categoryDto = new CategoryChildDto(null, "Regal", "Regal im Keller", parentId.toString());

        Category parent = new Category();
        parent.setId(parentId);
        parent.setName("Keller");

        Category newCategory = new Category();
        newCategory.setName("Regal");
        newCategory.setDescription("Regal im Keller");

        CategoryChildDto resultDto = new CategoryChildDto(categoryId.toString(), "Regal", "Regal im Keller", parentId.toString());

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(newCategory);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(newCategory)).thenReturn(category);
        when(categoryMapper.mapToChildDto(category)).thenReturn(resultDto);

        CategoryChildDto result = categoryService.createCategory(categoryDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(newCategory.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(newCategory);
        verify(categoryRepository).findById(parentId);
        verify(categoryRepository).save(newCategory);
    }

    @Test
    void createCategory_withUnknownParent_shouldThrow() {
        CategoryChildDto categoryDto = new CategoryChildDto(null, "Regal", "Regal im Keller", parentId.toString());

        Category newCategory = new Category();
        newCategory.setName("Regal");

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(newCategory);
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(categoryDto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateCategory_withParent_shouldSetParent() {
        CategoryChildDto categoryDto = new CategoryChildDto(categoryId.toString(), "Keller Updated", "Kellerraum", parentId.toString());

        Category parent = new Category();
        parent.setId(parentId);

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Keller Updated");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);

        CategoryChildDto resultDto = new CategoryChildDto(categoryId.toString(), "Keller Updated", "Kellerraum", parentId.toString());

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(updatedCategory);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.mapToChildDto(updatedCategory)).thenReturn(resultDto);

        CategoryChildDto result = categoryService.updateCategory(categoryDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(updatedCategory.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(updatedCategory);
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findById(parentId);
        verify(categoryRepository).save(updatedCategory);
    }

    @Test
    void updateCategory_withExistingParent_shouldRemoveFromOldParent() {
        CategoryChildDto categoryDto = new CategoryChildDto(categoryId.toString(), "Regal Updated", "Regal", null);

        Category oldParent = new Category();
        oldParent.setId(UUID.randomUUID());

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        oldParent.addChild(existingCategory);

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Regal Updated");

        CategoryChildDto resultDto = new CategoryChildDto(categoryId.toString(), "Regal Updated", "Regal", null);

        when(categoryMapper.mapToEntity(categoryDto)).thenReturn(updatedCategory);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.mapToChildDto(updatedCategory)).thenReturn(resultDto);

        CategoryChildDto result = categoryService.updateCategory(categoryDto);

        assertThat(result).isEqualTo(resultDto);
        assertThat(oldParent.getChildren()).doesNotContain(existingCategory);
        assertThat(existingCategory.getParent()).isNull();
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(updatedCategory);
    }

    @Test
    void deleteCategory_withoutParent_shouldDeleteById() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_withParent_shouldRemoveFromParentBeforeDelete() {
        Category parent = new Category();
        parent.setId(parentId);
        parent.addChild(category);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        assertThat(parent.getChildren()).doesNotContain(category);
        assertThat(category.getParent()).isNull();
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_shouldThrowWhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(categoryId))
                .isInstanceOf(NoSuchElementException.class);
    }
}
