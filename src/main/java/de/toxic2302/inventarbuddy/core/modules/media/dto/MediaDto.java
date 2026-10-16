package de.toxic2302.inventarbuddy.core.modules.media.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MediaDto(
        String id,

        @Size(max = 255)
        String fileName,

        String contentType,

        long fileSize,

        String itemId
) {
}
