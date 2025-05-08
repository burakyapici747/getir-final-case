package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;

import java.util.Set;
import java.util.UUID;

public interface AuthorService {
    PageableDto<AuthorDto> getAllAuthors(int currentPage, int pageSize);
    PageableDto<AuthorDto> searchAuthors(AuthorSearchCriteria authorSearchCriteria, int currentPage, int pageSize);
    AuthorDto getAuthorById(UUID id);
    Author getAuthorByIdOrElseThrow(UUID authorId);
    Set<Author> getAuthorsByIdsOrElseThrow(Set<UUID> authorIds);
    AuthorDto createAuthor(AuthorCreateRequest authorCreateRequest);
    AuthorDto updateAuthor(UUID authorId, AuthorUpdateRequest authorUpdateRequest);
    void addBookToAuthor(UUID authorId, UUID bookId);
    void deleteById(UUID id);
    void deleteBookFromAuthor(UUID authorId, UUID bookId);
}
