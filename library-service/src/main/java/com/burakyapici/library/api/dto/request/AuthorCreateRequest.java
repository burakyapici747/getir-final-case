package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AuthorCreateRequest(
    @NotBlank(message = "Author first name cannot be empty")
    @Size(min = 2, max = 50, message = "Author first name must be between 2 and 50 characters")
    String firstName,

    @NotBlank(message = "Author last name cannot be empty")
    @Size(min = 2, max = 50, message = "Author last name must be between 2 and 50 characters")
    String lastName,

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth
){}
