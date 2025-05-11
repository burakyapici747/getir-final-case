package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.PatronStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Email(message = "Email format is invalid")
    String email,

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters if provided")
    String firstName,

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters if provided")
    String lastName,

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number format is invalid")
    String phoneNumber,

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    String address,

    PatronStatus patronStatus
) {}