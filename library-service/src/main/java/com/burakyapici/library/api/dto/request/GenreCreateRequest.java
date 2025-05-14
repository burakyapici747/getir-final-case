package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GenreCreateRequest (
    @NotBlank(message = "Name cannot be blank")
    String name,
    @NotBlank(message = "Description cannot be blank")
    String description
){}
