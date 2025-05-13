package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookUpdateRequest(
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters if provided")
    String title,

    BookStatus bookStatus,

    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10000, message = "Page count cannot exceed 10000")
    int page,

    LocalDate publicationDate,

    Set<@NotNull(message = "Author ID cannot be null") UUID> authorIds,

    Set<@NotNull(message = "Genre ID cannot be null") UUID> genreIds
) {}