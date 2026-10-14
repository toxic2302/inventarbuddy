package de.toxic2302.inventarbuddy.core.modules.storage.controller;

import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageChildDto;
import de.toxic2302.inventarbuddy.core.modules.storage.dto.StorageParentDto;
import de.toxic2302.inventarbuddy.core.modules.storage.service.StorageService;
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
@RequestMapping("/storages")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @Operation(summary = "Get storage by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage found by id"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/{id}")
    public ResponseEntity<StorageParentDto> getStorageById(@PathVariable String id) {
        return ResponseEntity.ok().body(storageService.getStorage(UUID.fromString(id)));
    }

    @Operation(summary = "Get all storages by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storages found by user"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping()
    public ResponseEntity<List<StorageParentDto>> getAllStorages() {
        return ResponseEntity.ok().body(storageService.getAllStoragesByUser());
    }

    @Operation(summary = "Create storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage created"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping()
    public ResponseEntity<StorageChildDto> createStorage(@Valid @RequestBody StorageChildDto storageDto) {
        return ResponseEntity.ok().body(storageService.createStorage(storageDto));
    }

    @Operation(summary = "Updated storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage updated"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/update")
    public ResponseEntity<StorageChildDto> updateStorage(@Valid @RequestBody StorageChildDto storageDto) {
        return ResponseEntity.ok().body(storageService.updateStorage(storageDto));
    }

    @Operation(summary = "Delete storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Storage deleted"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStorage(@PathVariable String id) {
        storageService.deleteStorage(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
