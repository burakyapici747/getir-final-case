package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.UUID;

public record BookSearchCriteria(
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    String title,

    @Pattern(regexp = "^(?:\\d{10}|\\d{13})?$", message = "ISBN must be 10 or 13 digits if provided")
    String isbn,

    BookStatus bookStatus,

    @Min(value = 1, message = "Page count must be at least 1")
    @Max(value = 10000, message = "Page count cannot exceed 10000")
    Integer page,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate publicationDate,

    UUID genreId,

    UUID authorId
) {}