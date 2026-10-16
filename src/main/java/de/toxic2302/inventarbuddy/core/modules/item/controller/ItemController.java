package de.toxic2302.inventarbuddy.core.modules.item.controller;

import de.toxic2302.inventarbuddy.core.modules.item.dto.ItemDto;
import de.toxic2302.inventarbuddy.core.modules.item.service.ItemService;
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
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Get item by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item found by id"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable String id) {
        return ResponseEntity.ok().body(itemService.getItem(UUID.fromString(id)));
    }

    @Operation(summary = "Get all items by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items found by user"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping()
    public ResponseEntity<List<ItemDto>> getAllItems() {
        return ResponseEntity.ok().body(itemService.getAllItemsByUser());
    }

    @Operation(summary = "Create item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item created"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping()
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.createItem(itemDto));
    }

    @Operation(summary = "Update item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/update")
    public ResponseEntity<ItemDto> updateItem(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(itemDto));
    }

    @Operation(summary = "Delete item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item deleted"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        itemService.deleteItem(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
