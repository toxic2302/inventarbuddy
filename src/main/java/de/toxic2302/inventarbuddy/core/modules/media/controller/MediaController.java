package de.toxic2302.inventarbuddy.core.modules.media.controller;

import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaContentDto;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import de.toxic2302.inventarbuddy.core.modules.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @Operation(summary = "Get media metadata by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media found by id"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable String id) {
        return ResponseEntity.ok().body(mediaService.getMedia(UUID.fromString(id)));
    }

    @Operation(summary = "Get the content (image or PDF) of a media")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media content found"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping("/{id}/content")
    public ResponseEntity<byte[]> getMediaContent(@PathVariable String id) {
        final MediaContentDto content = mediaService.getMediaContent(UUID.fromString(id));
        final ResponseEntity.BodyBuilder response = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.contentType()));
        if (content.fileName() != null) {
            response.header(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.inline().filename(content.fileName()).build().toString());
        }
        return response.body(content.data());
    }

    @Operation(summary = "Get all media by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media found by user"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping()
    public ResponseEntity<List<MediaDto>> getAllMedia() {
        return ResponseEntity.ok().body(mediaService.getAllMediaByUser());
    }

    @Operation(summary = "Get all media of an item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media found by item"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<MediaDto>> getAllMediaByItem(@PathVariable String itemId) {
        return ResponseEntity.ok().body(mediaService.getAllMediaByItem(UUID.fromString(itemId)));
    }

    @Operation(summary = "Upload media (image or PDF), optionally linked to an item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media created"),
            @ApiResponse(responseCode = "400", description = "Invalid file"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaDto> createMedia(@RequestPart("file") MultipartFile file,
            @RequestParam(value = "itemId", required = false) String itemId) throws IOException {
        return ResponseEntity.ok().body(
                mediaService.createMedia(file.getOriginalFilename(), file.getBytes(), file.getContentType(), itemId));
    }

    @Operation(summary = "Update media file name and linked item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media updated"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @PostMapping("/update")
    public ResponseEntity<MediaDto> updateMedia(@Valid @RequestBody MediaDto mediaDto) {
        return ResponseEntity.ok().body(mediaService.updateMedia(mediaDto));
    }

    @Operation(summary = "Delete media")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media deleted"),
            @ApiResponse(responseCode = "500", description = "Unexpected exception")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable String id) {
        mediaService.deleteMedia(UUID.fromString(id));
        return ResponseEntity.ok().build();
    }
}
