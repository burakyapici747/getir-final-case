package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.api.dto.response.AuthorResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageableResponse<AuthorResponse>>> getAllAuthors(
        @RequestParam(value = "page", defaultValue = "0") int currentPage,
        @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        PageableResponse<AuthorResponse> authors = AuthorMapper.INSTANCE.pageableDtoToPageableResponse(
            authorService.getAllAuthors(currentPage, pageSize)
        );
        return ApiResponse.okResponse(authors, "Authors retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable("id") UUID id) {
        AuthorResponse author = AuthorMapper.INSTANCE.authorDtoToAuthorResponse(
            authorService.getAuthorById(id)
        );
        return ApiResponse.okResponse(author, "Author retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageableResponse<AuthorResponse>>> searchAuthors(
        @ModelAttribute AuthorSearchCriteria authorSearchCriteria,
        @RequestParam(value = "page", defaultValue = "0") int currentPage,
        @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        PageableResponse<AuthorResponse> authors = AuthorMapper.INSTANCE.pageableDtoToPageableResponse(
            authorService.searchAuthors(authorSearchCriteria, currentPage, pageSize)
        );
        return ApiResponse.okResponse(authors, "Authors retrieved successfully");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(@RequestBody AuthorCreateRequest authorCreateRequest) {
        AuthorResponse createdAuthor = AuthorMapper.INSTANCE.authorDtoToAuthorResponse(
            authorService.createAuthor(authorCreateRequest)
        );
        return ApiResponse.createdResponse(createdAuthor, "Author created successfully", createdAuthor.id());
    }

    @PostMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BookResponse>>> addBookToAuthor(
        @PathVariable("id") UUID id,
        @PathVariable("bookId") UUID bookId
    ) {
        List<BookResponse> books = BookMapper.INSTANCE.bookDtoListToBookResponseList(
            authorService.addBookToAuthor(id, bookId)
        );
        return ApiResponse.okResponse(books, "Book added to author successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
        @PathVariable("id") UUID id,
        @RequestBody AuthorUpdateRequest authorUpdateRequest
    ) {
        AuthorResponse updatedAuthor = AuthorMapper.INSTANCE.authorDtoToAuthorResponse(
            authorService.updateAuthor(id, authorUpdateRequest)
        );
        return ApiResponse.okResponse(updatedAuthor, "Author updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable("id") UUID id) {
        authorService.deleteAuthorByAuthorId(id);
        return ApiResponse.noContentResponse("Author deleted successfully");
    }

    @DeleteMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> removeBookFromAuthor(
        @PathVariable("id") UUID id,
        @PathVariable("bookId") UUID bookId
    ) {
        authorService.deleteBookFromAuthor(id, bookId);
        return ApiResponse.noContentResponse("Book removed from author successfully");
    }
}