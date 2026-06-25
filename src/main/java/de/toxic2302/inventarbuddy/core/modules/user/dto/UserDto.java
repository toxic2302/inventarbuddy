package de.toxic2302.inventarbuddy.core.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(String email, String firstName, String lastName) {
}
