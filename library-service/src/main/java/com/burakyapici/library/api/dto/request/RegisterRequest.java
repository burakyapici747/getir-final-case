package com.burakyapici.library.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must contain at least one letter and one number")
    String password,

    @NotBlank(message = "Password confirmation is required")
    String passwordConfirmation,

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    String lastName,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10,15}$", message = "Phone number must be between 10 and 15 digits")
    String phoneNumber,

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    String address
){}