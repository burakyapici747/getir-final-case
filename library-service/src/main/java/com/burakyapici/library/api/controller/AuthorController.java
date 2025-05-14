package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.response.AuthorResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@RequestMapping("/api/v1/authors")
@Tag(name = "Authors", description = "Endpoints for managing authors and their books")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @Operation(summary = "Get all authors", description = "Retrieves a paginated list of all authors.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<AuthorResponse>>> getAllAuthors(@Valid PageableParams params) {
        PageableDto<AuthorDto> pageResult = authorService.getAllAuthors(params.page(), params.size());
        PageableResponse<AuthorResponse> response = AuthorMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Authors retrieved successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID", description = "Retrieves author details by ID.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
        @Parameter(description = "UUID of the author") @PathVariable("id") UUID id
    ) {
        AuthorResponse author = AuthorMapper.INSTANCE.toAuthorResponse(authorService.getAuthorById(id));
        return ApiResponse.okResponse(author, "Author retrieved successfully");
    }

    @GetMapping("/search")
    @Operation(summary = "Search authors", description = "Search authors by criteria like name, etc.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authors retrieved successfully")
    public ResponseEntity<ApiResponse<PageableResponse<AuthorResponse>>> searchAuthors(
        @ModelAttribute AuthorSearchCriteria authorSearchCriteria,
        @Valid PageableParams params
    ) {
        PageableDto<AuthorDto> pageResult = authorService.searchAuthors(authorSearchCriteria, params.page(), params.size());
        PageableResponse<AuthorResponse> response = AuthorMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Authors retrieved successfully");
    }

    @PostMapping
    @Operation(summary = "Create author", description = "Creates a new author. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Author created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Author creation details",
            content = @Content(schema = @Schema(implementation = AuthorCreateRequest.class))
        )
        @RequestBody AuthorCreateRequest authorCreateRequest
    ) {
        AuthorResponse createdAuthor = AuthorMapper.INSTANCE.toAuthorResponse(
            authorService.createAuthor(authorCreateRequest)
        );
        return ApiResponse.createdResponse(createdAuthor, "Author created successfully", createdAuthor.id());
    }

    @PostMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @Operation(summary = "Add book to author", description = "Adds a book to the given author. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book added to author successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author or book not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<BookResponse>>> addBookToAuthor(
        @Parameter(description = "UUID of the author") @PathVariable("id") UUID id,
        @Parameter(description = "UUID of the book to add") @PathVariable("bookId") UUID bookId
    ) {
        List<BookResponse> books = BookMapper.INSTANCE.toResponse(
            authorService.addBookToAuthor(id, bookId)
        );
        return ApiResponse.okResponse(books, "Book added to author successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @Operation(summary = "Update author", description = "Updates an existing author's information. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Author updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
        @Parameter(description = "UUID of the author") @PathVariable("id") UUID id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Updated author details",
            content = @Content(schema = @Schema(implementation = AuthorUpdateRequest.class))
        )
        @RequestBody AuthorUpdateRequest authorUpdateRequest
    ) {
        AuthorResponse updatedAuthor = AuthorMapper.INSTANCE.toAuthorResponse(
                authorService.updateAuthor(id, authorUpdateRequest)
        );
        return ApiResponse.okResponse(updatedAuthor, "Author updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @Operation(summary = "Delete author", description = "Deletes an author by ID. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Author deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteById(
            @Parameter(description = "UUID of the author to delete") @PathVariable("id") UUID id
    ) {
        authorService.deleteAuthorByAuthorId(id);
        return ApiResponse.noContentResponse("Author deleted successfully");
    }

    @DeleteMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    @Operation(summary = "Remove book from author", description = "Removes a book from an author. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Book removed from author successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Author or book not found")
    })
    public ResponseEntity<ApiResponse<Void>> removeBookFromAuthor(
            @Parameter(description = "UUID of the author") @PathVariable("id") UUID id,
            @Parameter(description = "UUID of the book to remove") @PathVariable("bookId") UUID bookId
    ) {
        authorService.deleteBookFromAuthor(id, bookId);
        return ApiResponse.noContentResponse("Book removed from author successfully");
    }
}
