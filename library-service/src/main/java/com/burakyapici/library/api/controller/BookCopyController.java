package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.service.BookCopyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/book-copies")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public ResponseEntity<PageableResponse<BookCopyResponse>> getAllBookCopies(
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
                bookCopyService.getAllBookCopies(currentPage, pageSize)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopyResponse> getBookCopyById(@PathVariable UUID id) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(bookCopyService.getBookCopyById(id))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PageableResponse<BookCopyResponse>> searchBookCopies(
        @ModelAttribute BookCopySearchCriteria bookCopySearchCriteria,
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
                bookCopyService.searchBookCopies(bookCopySearchCriteria, currentPage, pageSize)
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookCopyResponse> createBookCopy(@RequestBody BookCopyCreateRequest bookCopyCreateRequest) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(bookCopyService.createBookCopy(bookCopyCreateRequest))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookCopyResponse> updateBookCopy(
        @PathVariable UUID id,
        @RequestBody BookCopyUpdateRequest bookCopyUpdateRequest
    ) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyDtoToBookCopyResponse(
                bookCopyService.updateBookCopyById(id, bookCopyUpdateRequest)
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<Void> deleteBookCopyById(@PathVariable UUID id) {
        bookCopyService.deleteBookCopyById(id);
        return ResponseEntity.noContent().build();
    }
}
