package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.BookCopy;

import java.util.UUID;

public interface BookCopyService {
    BookCopy getBookCopyByIdOrElseThrow(UUID id);
    BookCopy getBookCopyByBarcodeOrElseThrow(String barcode);
    PageableDto<BookCopyDto> getAllBookCopies(int currentPage, int pageSize);
    PageableDto<BookCopyDto> getAllBookCopiesByBookId(UUID bookId, int currentPage, int pageSize);
    BookCopyDto createBookCopy(BookCopyCreateRequest bookCopyCreateRequest);
    BookCopyDto updateBookCopyById(UUID id, BookCopyUpdateRequest bookCopyUpdateRequest);
    BookCopyDto getBookCopyById(UUID id);
    void deleteBookCopyById(UUID id);
    int countByIdAndStatus(UUID id, BookCopyStatus status);
    BookCopy saveBookCopy(BookCopy bookCopy);
    PageableDto<BookCopyDto> searchBookCopies(BookCopySearchCriteria bookCopySearchCriteria, int currentPage, int pageSize);
    void deleteAllByBookId(UUID bookId);
}
