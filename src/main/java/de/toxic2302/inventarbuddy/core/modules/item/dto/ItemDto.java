package de.toxic2302.inventarbuddy.core.modules.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.toxic2302.inventarbuddy.core.modules.media.dto.MediaDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItemDto(
        String id,

        @NotBlank(message = "Name must not be blank")
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @Size(max = 255)
        String serialNumber,

        @Size(max = 255)
        String modelNumber,

        @Size(max = 255)
        String manufacturer,

        @Min(value = 0, message = "Quantity must not be negative")
        Integer quantity,

        Boolean sold,

        String categoryId,

        String storageId,

        List<MediaDto> media
) {
}
