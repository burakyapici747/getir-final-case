package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/v1/books")
@Tag(name = "Books", description = "Operations related to books in the library")
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
    @Operation(summary = "Get all books", description = "Retrieves a paginated list of all books.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> getAllBooks(@Valid PageableParams params) {
        PageableDto<BookDto> pageResult = bookService.getAllBooks(params.page(), params.size());
        PageableResponse<BookResponse> response = BookMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Books retrieved successfully.");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book detail", description = "Returns detailed information about a specific book.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book detail retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<BookDetailResponse>> getBookDetail(
        @Parameter(description = "Book UUID") @PathVariable("id") @NotNull UUID bookId
    ) {
        BookDetailDto dto = bookService.getBookDetailById(bookId);
        BookDetailResponse response = BookMapper.INSTANCE.toDetailResponse(dto);
        return ApiResponse.okResponse(response, "Book detail retrieved successfully.");
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Searches for books using filters like title, author, etc.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<BookResponse>>> searchBooks(
        @Valid @ModelAttribute BookSearchCriteria criteria,
        @Valid PageableParams params
    ) {
        PageableDto<BookDto> pageResult = bookService.searchBooks(criteria, params.page(), params.size());
        PageableResponse<BookResponse> response = BookMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Search results retrieved successfully.");
    }

    @GetMapping("/{id}/copies")
    @Operation(summary = "Get copies of a book", description = "Retrieves all copies of a specific book.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copies retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getBookCopies(
        @Parameter(description = "Book UUID") @PathVariable("id") @NotNull UUID bookId,
        @Valid PageableParams params
    ) {
        PageableDto<BookCopyDto> pageResult = bookService.getBookCopiesById(bookId, params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Book copies retrieved successfully.");
    }

    @GetMapping(value = "/{id}/book-availability", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
        summary = "Stream book availability updates",
        description = "Streams real-time book availability as server-sent events (SSE)."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book availability stream started", content = @Content(mediaType = "text/event-stream"))
    public Flux<BookAvailabilityUpdateEvent> streamBookAvailabilityUpdates(
        @Parameter(description = "Book UUID") @PathVariable(name = "id") @NotNull UUID id
    ) {
        int initialValue = bookService.calculateAvailableCopiesCount(id);
        BookAvailabilityUpdateEvent initialEvent = new BookAvailabilityUpdateEvent(id, initialValue);
        bookAvailabilitySink.tryEmitNext(initialEvent);
        return bookAvailabilityFlux.filter(event -> event.bookId().equals(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Create a new book", description = "Adds a new book to the library. Only librarians can perform this operation.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse createdBook = BookMapper.INSTANCE.toResponse(bookService.createBook(request));
        return ApiResponse.createdResponse(createdBook, "Book created successfully.", createdBook.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Update a book", description = "Updates book details. Only librarians can perform this operation.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
        @Parameter(description = "Book UUID") @PathVariable("id") @NotNull UUID id,
        @Valid @RequestBody BookUpdateRequest request
    ) {
        BookResponse updatedBook = BookMapper.INSTANCE.toResponse(bookService.updateBook(id, request));
        return ApiResponse.okResponse(updatedBook, "Book updated successfully.");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Delete a book", description = "Deletes a book from the library. Only librarians can perform this operation.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBook(
        @Parameter(description = "Book UUID") @PathVariable("id") @NotNull UUID id
    ) {
        bookService.deleteById(id);
        return ApiResponse.noContentResponse("Book deleted successfully.");
    }
}
