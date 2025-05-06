package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.repository.AuthorRepository;
import com.burakyapici.library.exception.AuthorNotFoundException;
import com.burakyapici.library.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author getAuthorByIdOrElseThrow(UUID id) {
        return authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    }

    @Override
    public Set<Author> getAuthorsByIdsOrElseThrow(Set<UUID> authorIds) {
        List<Author> authors = authorRepository.findAllById(authorIds);

        if (authors.size() != authorIds.size()) {
            Set<UUID> foundAuthorIds = authors.stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

            Set<UUID> missingAuthorIds = authorIds.stream()
                .filter(id -> !foundAuthorIds.contains(id))
                .collect(Collectors.toSet());

            throw new AuthorNotFoundException(
                "The following author IDs could not be found: " +
                    missingAuthorIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining(", "))
            );
        }

        return new HashSet<>(authors);
    }

    @Override
    public AuthorDto createAuthor(AuthorCreateRequest authorCreateRequest) {
        Author author = Author.builder()
            .firstName(authorCreateRequest.firstName())
            .lastName(authorCreateRequest.lastName())
            .build();

        return AuthorMapper.INSTANCE.toDto(authorRepository.save(author));
    }

    @Override
    public void deleteById(UUID id) {
        Author author = getAuthorByIdOrElseThrow(id);
        authorRepository.delete(author);
    }
}
