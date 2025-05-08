package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.api.dto.response.AuthorResponse;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable UUID id) {
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
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorCreateRequest authorCreateRequest) {
        return ResponseEntity.ok(authorService.createAuthor(authorCreateRequest));
    }

    @PostMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<Void> addBookToAuthor(@PathVariable UUID id, @PathVariable UUID bookId) {
        authorService.addBookToAuthor(id, bookId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<AuthorDto> updateAuthor(
        @PathVariable UUID id,
        @RequestBody AuthorUpdateRequest authorUpdateRequest
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        authorService.deleteById(id);
    }

    @DeleteMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_LIBRARIAN')")
    public ResponseEntity<Void> removeBookFromAuthor(@PathVariable UUID id, @PathVariable UUID bookId) {
        authorService.deleteBookFromAuthor(id, bookId);
        return ResponseEntity.ok().build();
    }
}
