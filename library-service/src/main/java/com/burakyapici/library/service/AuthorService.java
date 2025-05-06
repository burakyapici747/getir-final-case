package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.model.Author;

import java.util.Set;
import java.util.UUID;

public interface AuthorService {
    Author getAuthorByIdOrElseThrow(UUID authorId);
    Set<Author> getAuthorsByIdsOrElseThrow(Set<UUID> authorIds);
    AuthorDto createAuthor(AuthorCreateRequest authorCreateRequest);
    void deleteById(UUID id);
}
