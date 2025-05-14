package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.response.BookCopyResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.BookCopyMapper;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.service.BookCopyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/api/v1/book-copies")
@Tag(name = "Book Copies", description = "Endpoints for managing individual book copies")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    @Operation(summary = "Get all book copies", description = "Retrieves all book copies in paginated form.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copies retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> getAllBookCopies(@Valid PageableParams params) {
        PageableDto<BookCopyDto> pageResult = bookCopyService.getAllBookCopies(params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Book copies retrieved successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book copy by ID", description = "Retrieves a specific book copy by its UUID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copy retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book copy not found")
    })
    public ResponseEntity<ApiResponse<BookCopyResponse>> getBookCopyById(
        @Parameter(description = "UUID of the book copy") @PathVariable("id") UUID id
    ) {
        BookCopyDto dto = bookCopyService.getBookCopyById(id);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);
        return ApiResponse.okResponse(response, "Book copy retrieved successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search book copies", description = "Searches book copies based on filter criteria.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copies retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<BookCopyResponse>>> searchBookCopies(
        @Valid @ModelAttribute BookCopySearchCriteria bookCopySearchCriteria,
        @Valid PageableParams params
    ) {
        PageableDto<BookCopyDto> pageResult =
            bookCopyService.searchBookCopies(bookCopySearchCriteria, params.page(), params.size());
        PageableResponse<BookCopyResponse> response = BookCopyMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Book copies retrieved successfully");
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Create a book copy", description = "Creates a new book copy. Only librarians can access this.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book copy created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BookCopyResponse>> createBookCopy(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Book copy creation data",
            content = @Content(schema = @Schema(implementation = BookCopyCreateRequest.class))
        )
        @Valid @RequestBody BookCopyCreateRequest bookCopyCreateRequest
    ) {
        BookCopyDto dto = bookCopyService.createBookCopy(bookCopyCreateRequest);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);
        return ApiResponse.createdResponse(response, "Book copy created successfully", response.id());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Update a book copy", description = "Updates an existing book copy. Only librarians can access this.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book copy updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book copy not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<BookCopyResponse>> updateBookCopy(
        @Parameter(description = "UUID of the book copy") @PathVariable("id") @NotNull UUID id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Book copy update data",
            content = @Content(schema = @Schema(implementation = BookCopyUpdateRequest.class))
        )
        @Valid @RequestBody BookCopyUpdateRequest bookCopyUpdateRequest
    ) {
        BookCopyDto dto = bookCopyService.updateBookCopyById(id, bookCopyUpdateRequest);
        BookCopyResponse response = BookCopyMapper.INSTANCE.toBookCopyResponse(dto);
        return ApiResponse.okResponse(response, "Book copy updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Delete a book copy", description = "Deletes a book copy by ID. Only librarians can access this.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Book copy deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book copy not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBookCopyById(
        @Parameter(description = "UUID of the book copy") @PathVariable("id") UUID id
    ) {
        bookCopyService.deleteBookCopyById(id);
        return ApiResponse.noContentResponse("Book copy deleted successfully");
    }
}
