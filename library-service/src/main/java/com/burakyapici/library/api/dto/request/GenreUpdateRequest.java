package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record GenreUpdateRequest(
    @NotBlank(message = "Name cannot be null")
    @Max(value = 50, message = "Name cannot be longer than 50 characters")
    String name,
    @NotBlank(message = "Description cannot be null")
    @Max(value = 255, message = "Description cannot be longer than 255 characters")
    String description
) {}
