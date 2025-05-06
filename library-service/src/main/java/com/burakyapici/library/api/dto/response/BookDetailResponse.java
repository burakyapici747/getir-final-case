package com.burakyapici.library.api.dto.response;

import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.model.WaitList;

import java.time.LocalDateTime;
import java.util.List;

public record BookDetailResponse(
    String title,
    String isbn,
    BookStatus bookStatus,
    int page,
    LocalDateTime publicationDate,
    List<Author> authors,
    List<WaitList> waitLists,
    List<Genre> genreList,
    List<BookCopy> bookCopyList
) {}
