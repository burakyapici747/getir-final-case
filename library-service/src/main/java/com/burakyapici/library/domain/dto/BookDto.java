package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.List;

public record BookDto(
    String id,
    String title,
    String isbn,
    BookStatus bookStatus,
    LocalDate publicationDate,
    int page,
    List<GenreDto> genres,
    List<AuthorDto> authors
){}
