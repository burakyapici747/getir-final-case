package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AuthorUpdateRequest(
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters if provided")
    String firstName,

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters if provided")
    String lastName,

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth
) {}