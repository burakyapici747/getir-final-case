package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Book;

import java.util.UUID;

public interface BookService {
    PageableDto<BookDto> getAllBooks(int currentPage, int pageSize);
    Book getBookByIdOrElseThrow(UUID id);
    PageableDto<BookCopyDto> getBookCopiesById(UUID id, int currentPage, int pageSize);
    BookDetailDto getBookDetailById(UUID id);
    BookDto createBook(BookCreateRequest bookCreateRequest);
    BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest);
    PageableDto<BookDto> searchBooks(BookSearchCriteria bookSearchCriteria, int currentPage, int pageSize);
    int calculateAvailableCopiesCount(UUID bookId);
    void deleteBook(UUID id);
}
