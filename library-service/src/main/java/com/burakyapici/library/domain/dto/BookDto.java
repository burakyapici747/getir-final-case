package com.burakyapici.library.domain.dto;

import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.model.WaitList;

import java.util.List;
import java.util.Set;

public record BookDto(
    String title,
    String isbn,
    int page,
    List<Genre> genres,
    Set<Author> author
){}
