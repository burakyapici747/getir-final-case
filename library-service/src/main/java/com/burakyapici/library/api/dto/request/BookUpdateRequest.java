package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record BookUpdateRequest(
    String title,
    BookStatus bookStatus,
    int page,
    LocalDate publicationDate,
    Set<UUID> authorIds,
    Set<UUID> genreIds
) {}
