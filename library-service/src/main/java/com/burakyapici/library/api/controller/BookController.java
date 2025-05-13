package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.*;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.BookDetailResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> getAllBooks(@Valid PageableParams params) {
        PageableDto<BookDto> pageResult = bookService.getAllBooks(params.page(), params.size());
        PageableResponse<BookResponse> response = BookMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Books retrieved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetail(@PathVariable("id") @NotNull UUID bookId) {
        BookDetailDto dto = bookService.getBookDetailById(bookId);
        BookDetailResponse response = BookMapper.INSTANCE.toDetailResponse(dto);

        return ApiResponse.okResponse(response, "Book detail retrieved successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> searchBooks(
        @Valid @ModelAttribute BookSearchCriteria criteria,
        @Valid PageableParams params
    ) {
        PageableDto<BookDto> pageResult = bookService.searchBooks(criteria, params.page(), params.size());
        PageableResponse<BookResponse> response = BookMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Search results retrieved successfully.");
    }

    @GetMapping("/{id}/copies")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getBookCopies(
        @PathVariable("id") @NotNull UUID bookId,
        @Valid PageableParams params
    ) {
        PageableDto<BookCopyDto> pageResult = bookService.getBookCopiesById(bookId, params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);

        return ApiResponse.okResponse(response, "Book copies retrieved successfully.");
    }

    @GetMapping(value = "/{id}/book-availability", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookAvailabilityUpdateEvent> streamBookAvailabilityUpdates(@PathVariable(name = "id") @NotNull UUID id) {
        int initialValue = bookService.calculateAvailableCopiesCount(id);

        BookAvailabilityUpdateEvent initialEvent = new BookAvailabilityUpdateEvent(id, initialValue);
        bookAvailabilitySink.tryEmitNext(initialEvent);

        return bookAvailabilityFlux.filter(event -> event.bookId().equals(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse createdBook = BookMapper.INSTANCE.toResponse(bookService.createBook(request));

        return ApiResponse.createdResponse(createdBook, "Book created successfully.", createdBook.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @PathVariable("id") @NotNull UUID id,
        @Valid @RequestBody BookUpdateRequest request
    ) {
        BookResponse updatedBook = BookMapper.INSTANCE.toResponse(bookService.updateBook(id, request));

        return ApiResponse.okResponse(updatedBook, "Book updated successfully.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable("id") @NotNull UUID id) {
        bookService.deleteById(id);

        return ApiResponse.noContentResponse("Book deleted successfully.");
    }
}