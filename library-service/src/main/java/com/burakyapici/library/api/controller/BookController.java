package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Validated
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
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> getAllBooks(
        @Valid
        @Min(value = 0)
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @Valid
        @Min(value = 1)
        @Max(value = 50)
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<BookResponse> books = BookMapper.INSTANCE.bookPageableDtoListToPageableResponse(
            bookService.getAllBooks(currentPage, pageSize)
        );
        return ApiResponse.okResponse(books, "Books retrieved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetailById(@PathVariable("id") UUID id) {
        BookDetailResponse book = BookMapper.INSTANCE.bookDetailDtoToBookDetailResponse(
            bookService.getBookDetailById(id)
        );
        return ApiResponse.okResponse(book, "Book detail retrieved successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> searchBooks(
        @ModelAttribute BookSearchCriteria bookSearchCriteria,
        @RequestParam(name = "currentPage", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ) {
        PageableResponse<BookResponse> books = BookMapper.INSTANCE.bookPageableDtoListToPageableResponse(
            bookService.searchBooks(bookSearchCriteria, currentPage, pageSize)
        );
        return ApiResponse.okResponse(books, "Books retrieved successfully.");
    }

    @GetMapping("/{id}/copies")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getBookCopies(
        @PathVariable("id") UUID id,
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10",required = false) int pageSize
    ) {
        PageableResponse<BookCopyResponse> copies = BookCopyMapper.INSTANCE.bookCopyPageableDtoListToPageableResponse(
            bookService.getBookCopiesById(id, currentPage, pageSize)
        );
        return ApiResponse.okResponse(copies, "Book copies retrieved successfully.");
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
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookCreateRequest bookCreateRequest) {
        BookResponse createdBook = BookMapper.INSTANCE.bookDtoToBookResponse(
            bookService.createBook(bookCreateRequest)
        );

        return ApiResponse.createdResponse(createdBook, "Book created successfully.", createdBook.id());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable("id") UUID id,
        @RequestBody BookUpdateRequest bookUpdateRequest
    ) {
        BookResponse updatedBook = BookMapper.INSTANCE.bookDtoToBookResponse(
            bookService.updateBook(id, bookUpdateRequest)
        );
        return ApiResponse.okResponse(updatedBook, "Book updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("id") UUID id) {
        bookService.deleteBookById(id);
        return ApiResponse.noContentResponse("Book deleted successfully.");
    }
}