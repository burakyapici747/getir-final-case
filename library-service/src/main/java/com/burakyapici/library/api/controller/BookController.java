package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.response.BookDetailResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookDetailDtoToBookDetailResponse(bookService.getBookDetailById(id))
        );
    }

    @GetMapping
    public ResponseEntity<PageableResponse<BookResponse>> getAllBooks(
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size",required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookPageableDtoListToPageableResponse(bookService.getAllBooks(currentPage, pageSize))
        );
    }

    //TODO: Search işlemi eklenecek ve Pageable kullanılacak.

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookResponse> createBook(@RequestBody BookCreateRequest bookCreateRequest) {
        return ResponseEntity.ok(BookMapper.INSTANCE.bookDtoToBookResponse(bookService.createBook(bookCreateRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable UUID id, @RequestBody BookCreateRequest bookCreateRequest) {
        return ResponseEntity.ok("Book updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<?> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }

}
