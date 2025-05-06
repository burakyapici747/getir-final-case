package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Book;

import java.util.UUID;

public interface BookService {
    PageableDto<BookDto> getAllBooks(int currentPage, int pageSize);
    Book getBookByIdOrElseThrow(UUID id);
    BookDetailDto getBookDetailById(UUID id);
    BookDto createBook(BookCreateRequest bookCreateRequest);
    BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest);
    void deleteBook(UUID id);
}
