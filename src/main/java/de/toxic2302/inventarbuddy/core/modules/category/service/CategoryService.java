package de.toxic2302.inventarbuddy.core.modules.category.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryChildDto;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryParentDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.mapper.CategoryMapper;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final AuthenticatedUserService authenticatedUserService;

    public CategoryParentDto getCategory(UUID id) {
        return categoryMapper.mapToDto(categoryRepository.findById(id).orElseThrow());
    }

    public List<CategoryParentDto> getAllCategoriesByUser() {
        return categoryMapper.mapToDtoList(
                categoryRepository.findAllByUserAndParentIsNull(authenticatedUserService.getCurrentUser()));
    }

    @Transactional
    public CategoryChildDto createCategory(CategoryChildDto categoryDto) {
        final Category newCategory = categoryMapper.mapToEntity(categoryDto);
        newCategory.setUser(authenticatedUserService.getCurrentUser());

        if (categoryDto.parentId() != null) {
            final Category parent = categoryRepository.findById(UUID.fromString(categoryDto.parentId())).orElseThrow();
            parent.addChild(newCategory);
        }

        return categoryMapper.mapToChildDto(categoryRepository.save(newCategory));
    }

    @Transactional
    public CategoryChildDto updateCategory(CategoryChildDto categoryDto) {
        final Category category = categoryMapper.mapToEntity(categoryDto);
        final Category existingCategory = categoryRepository.findById(category.getId()).orElseThrow();

        final Category oldParent = existingCategory.getParent();
        if (oldParent != null) {
            oldParent.removeChild(existingCategory);
        }

        if (categoryDto.parentId() != null) {
            final Category parent = categoryRepository.findById(UUID.fromString(categoryDto.parentId())).orElseThrow();
            parent.addChild(category);
        }

        return categoryMapper.mapToChildDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        final Category category = categoryRepository.findById(id).orElseThrow();

        final Category parent = category.getParent();
        if (parent != null) {
            parent.removeChild(category);
        }

        categoryRepository.delete(category);
    }
}
