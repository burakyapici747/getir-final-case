package com.burakyapici.library.domain.dto;

import java.time.LocalDate;
import java.util.Set;

public record AuthorDto(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    Set<BookDto> bookList
) {}
