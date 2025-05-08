package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.BookDetailResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final Flux<BookAvailabilityUpdateEvent> bookAvailabilityFlux;
    private final Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink;

    public BookController(
        BookService bookService,
        Flux<BookAvailabilityUpdateEvent> bookAvailabilityFlux,
        Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink
    ) {
        this.bookService = bookService;
        this.bookAvailabilityFlux = bookAvailabilityFlux;
        this.bookAvailabilitySink = bookAvailabilitySink;
    }

    @GetMapping
    public ResponseEntity<PageableResponse<BookResponse>> getAllBooks(
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookPageableDtoListToPageableResponse(bookService.getAllBooks(currentPage, pageSize))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponse> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookDetailDtoToBookDetailResponse(bookService.getBookDetailById(id))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PageableResponse<BookResponse>> searchBooks(
        @ModelAttribute BookSearchCriteria bookSearchCriteria,
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookPageableDtoListToPageableResponse(
                bookService.searchBooks(bookSearchCriteria, currentPage, pageSize)
            )
        );
    }

    @GetMapping("/{id}/copies")
    public ResponseEntity<PageableResponse<BookCopyResponse>> getBookCopies(
        @PathVariable UUID id,
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10",required = false) int pageSize
    ) {
        return ResponseEntity.ok(
            BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
                bookService.getBookCopiesById(id, currentPage, pageSize)
            )
        );
    }

    @GetMapping(value = "/{id}/book-availability", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookAvailabilityUpdateEvent> streamBookAvailabilityUpdates(@PathVariable(name = "id") UUID id) {
        int initialValue = bookService.calculateAvailableCopiesCount(id);

        BookAvailabilityUpdateEvent initialEvent = new BookAvailabilityUpdateEvent(id, initialValue);
        bookAvailabilitySink.tryEmitNext(initialEvent);

        return bookAvailabilityFlux.filter(event -> event.bookId().equals(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookResponse> createBook(@RequestBody BookCreateRequest bookCreateRequest) {
        return ResponseEntity.ok(BookMapper.INSTANCE.bookDtoToBookResponse(bookService.createBook(bookCreateRequest)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID id, @RequestBody BookUpdateRequest bookUpdateRequest) {
        return ResponseEntity.ok(BookMapper.INSTANCE.bookDtoToBookResponse(bookService.updateBook(id, bookUpdateRequest)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<?> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully");
    }
}
