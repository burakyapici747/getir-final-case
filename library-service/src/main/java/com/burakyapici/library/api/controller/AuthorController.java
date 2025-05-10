package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.api.dto.response.AuthorResponse;
import com.burakyapici.library.api.dto.response.BookResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.service.AuthorService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<PageableResponse<AuthorResponse>> getAllAuthors(
        @RequestParam(value = "page", defaultValue = "0") int currentPage,
        @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(
            AuthorMapper.INSTANCE.pageableDtoToPageableResponse(authorService.getAllAuthors(currentPage, pageSize))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(AuthorMapper.INSTANCE.authorDtoToAuthorResponse(authorService.getAuthorById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<PageableResponse<AuthorResponse>> searchAuthors(
        @ModelAttribute AuthorSearchCriteria authorSearchCriteria,
        @RequestParam(value = "page", defaultValue = "0") int currentPage,
        @RequestParam(value = "size", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(
            AuthorMapper.INSTANCE.pageableDtoToPageableResponse(
                authorService.searchAuthors(authorSearchCriteria, currentPage, pageSize)
            )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<AuthorResponse> createAuthor(@RequestBody AuthorCreateRequest authorCreateRequest) {
        return ResponseEntity.ok(
            AuthorMapper.INSTANCE.authorDtoToAuthorResponse(authorService.createAuthor(authorCreateRequest))
        );
    }

    @PostMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<List<BookResponse>> addBookToAuthor(
        @PathVariable("id") UUID id,
        @PathVariable("bookId") UUID bookId
    ) {
        return ResponseEntity.ok(
            BookMapper.INSTANCE.bookDtoListToBookResponseList(authorService.addBookToAuthor(id, bookId))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<AuthorResponse> updateAuthor(
        @PathVariable("id") UUID id,
        @RequestBody AuthorUpdateRequest authorUpdateRequest
    ) {
        return ResponseEntity.ok(
            AuthorMapper.INSTANCE.authorDtoToAuthorResponse(authorService.updateAuthor(id, authorUpdateRequest))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") UUID id) {
        authorService.deleteAuthorByAuthorId(id);
    }

    @DeleteMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<Void> removeBookFromAuthor(@PathVariable("id") UUID id, @PathVariable("bookId") UUID bookId) {
        authorService.deleteBookFromAuthor(id, bookId);
        return ResponseEntity.ok().build();
    }
}
