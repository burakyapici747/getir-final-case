package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record BookUpdateRequest(
    String title,
    String isbn,
    BookStatus bookStatus,
    int page,
    LocalDateTime publicationDate,
    Set<UUID> authorIds,
    Set<UUID> genreIds
) {}
