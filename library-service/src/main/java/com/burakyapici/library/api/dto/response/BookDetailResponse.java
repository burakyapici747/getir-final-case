package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.GenreDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.BookStatus;

import java.time.LocalDate;
import java.util.List;

public record BookDetailResponse(
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

