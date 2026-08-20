package de.toxic2302.inventarbuddy.core.modules.category.service;

import de.toxic2302.inventarbuddy.base.authentication.AuthenticatedUserService;
import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import de.toxic2302.inventarbuddy.core.modules.category.entity.Category;
import de.toxic2302.inventarbuddy.core.modules.category.mapper.CategoryMapper;
import de.toxic2302.inventarbuddy.core.modules.category.repository.CategoryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final AuthenticatedUserService authenticatedUserService;

    public CategoryDto getCategory(UUID id) {
        return categoryMapper.mapToDto(categoryRepository.findById(id).orElseThrow());
    }

    public List<CategoryDto> getAllCategoriesByUser() {
        return categoryMapper.mapToDtoList(categoryRepository.findAllByUser(authenticatedUserService.getCurrentUser()));
    }

    public CategoryDto createCategory(CategoryDto category) {
        final Category newCategory = categoryMapper.mapToEntity(category);
        newCategory.setUser(authenticatedUserService.getCurrentUser());

        return categoryMapper.mapToDto(categoryRepository.save(newCategory));
    }

    public CategoryDto updateCategory(CategoryDto category) {
        return categoryMapper.mapToDto(categoryRepository.save(categoryMapper.mapToEntity(category)));
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
