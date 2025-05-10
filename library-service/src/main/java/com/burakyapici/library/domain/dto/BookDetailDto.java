package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.List;

public record BookDetailDto(
    String id,
    String title,
    String isbn,
    BookStatus bookStatus,
    int page,
    LocalDate publicationDate,
    int availableCopies,
    List<AuthorDto> authors,
    List<WaitListDto> waitLists,
    List<GenreDto> genres,
    List<BookCopyDto> bookCopies
) {}