package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.dto.BookDto;

import java.time.LocalDate;
import java.util.Set;

public record AuthorResponse(
    String firstName,
    String lastName,
    LocalDate birthOfDate,
    Set<BookDto> bookList
) {}
