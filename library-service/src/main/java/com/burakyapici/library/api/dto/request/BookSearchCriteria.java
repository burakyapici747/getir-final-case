package com.burakyapici.library.api.dto.request;

import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.UUID;

public record BookSearchCriteria(
    String title,
    String isbn,
    BookStatus bookStatus,
    int page,
    LocalDate publicationDate,
    UUID genreId,
    UUID authorId
) {}
