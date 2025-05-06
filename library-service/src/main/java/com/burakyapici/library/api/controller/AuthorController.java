package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorCreateRequest authorCreateRequest) {
        return ResponseEntity.ok(authorService.createAuthor(authorCreateRequest));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id){
        authorService.deleteById(id);
    }
}
