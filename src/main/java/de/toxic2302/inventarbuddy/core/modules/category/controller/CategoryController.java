package de.toxic2302.inventarbuddy.core.modules.category.controller;

import de.toxic2302.inventarbuddy.core.modules.category.dto.CategoryDto;
import de.toxic2302.inventarbuddy.core.modules.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found by id"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok().body(categoryService.getCategory(UUID.fromString(id)));
    }

    @Operation(summary = "Get all categories by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories found by user"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAllCategoriesByUser());
    }

    @Operation(summary = "Create category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping()
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoryService.createCategory(categoryDto));
    }

    @Operation(summary = "Updated category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/update")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryDto));
    }

    @Operation(summary = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
