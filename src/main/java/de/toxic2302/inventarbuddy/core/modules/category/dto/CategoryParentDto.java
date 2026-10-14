package de.toxic2302.inventarbuddy.core.modules.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryParentDto(
        String id,

        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        List<CategoryParentDto> children
) {
}
