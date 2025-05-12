package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookCreateRequest(
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title,

    @NotBlank(message = "ISBN cannot be empty")
    @Pattern(
        regexp = "^(?:ISBN(?:-1[03])?:?\\s)?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
        message = "ISBN format is invalid"
    )
    String isbn,

    @NotNull(message = "Book status cannot be null")
    BookStatus bookStatus,

    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10000, message = "Page count cannot exceed 10000")
    int page,

    @NotNull(message = "Publication date cannot be null")
    LocalDate publicationDate,

    @NotEmpty(message = "At least one author must be specified")
    Set<@NotNull(message = "Author ID cannot be null") UUID> authorIds,

    @NotEmpty(message = "At least one genre must be specified")
    Set<@NotNull(message = "Genre ID cannot be null") UUID> genreIds
) {}