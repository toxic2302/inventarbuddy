package de.toxic2302.inventarbuddy.core.modules.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDto(
        @NotBlank(message = "Name darf nicht leer sein")
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description
) {
}
