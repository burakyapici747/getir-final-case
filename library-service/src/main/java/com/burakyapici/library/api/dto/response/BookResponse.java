package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookResponse(
    UUID id,
    String title,
    String isbn,
    BookStatus bookStatus,
    LocalDate publicationDate,
    int page,
    List<GenreDto> genres,
    List<AuthorDto> authors
) {}
