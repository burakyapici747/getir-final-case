package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.common.mapper.AuthorMapper;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.repository.AuthorRepository;
import com.burakyapici.library.domain.specification.AuthorSpecifications;
import com.burakyapici.library.service.AuthorService;
import com.burakyapici.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {
    private static final int TOTAL_ELEMENTS_PER_PAGE = 10;
    private final AuthorRepository authorRepository;
    private final BookService bookService;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookService = bookService;
    }

    @Override
    public PageableDto<AuthorDto> getAllAuthors(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Author> allAuthorsPage = authorRepository.findAll(pageable);
        List<AuthorDto> authorDtoList = AuthorMapper.INSTANCE.authorListToAuthorDtoList(allAuthorsPage.getContent());

        return new PageableDto<>(
            authorDtoList,
            allAuthorsPage.getTotalPages(),
            TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            allAuthorsPage.hasNext(),
            allAuthorsPage.hasPrevious()
        );
    }

    @Override
    public PageableDto<AuthorDto> searchAuthors(AuthorSearchCriteria authorSearchCriteria, int currentPage, int pageSize) {
        Specification<Author> spec = AuthorSpecifications.findByCriteria(authorSearchCriteria);

        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Author> allAuthorPage = authorRepository.findAll(spec, pageable);

        List<AuthorDto> authorDtoList = AuthorMapper.INSTANCE.authorListToAuthorDtoList(allAuthorPage.getContent());

        return new PageableDto<>(
            authorDtoList,
            allAuthorPage.getTotalPages(),
            allAuthorPage.getSize(),
            allAuthorPage.getNumber(),
            allAuthorPage.hasNext(),
            allAuthorPage.hasPrevious()
        );
    }

    @Override
    public AuthorDto getAuthorById(UUID id) {
        Author author = findByIdOrElseThrow(id);
        return AuthorMapper.INSTANCE.authorToAuthorDto(author);
    }

    @Override
    public Author getAuthorByIdOrElseThrow(UUID id) {
        return findByIdOrElseThrow(id);
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

            throw new EntityNotFoundException(
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
            .dateOfBirth(authorCreateRequest.dateOfBirth())
            .build();

        return AuthorMapper.INSTANCE.authorToAuthorDto(authorRepository.save(author));
    }

    @Override
    public AuthorDto updateAuthor(UUID authorId, AuthorUpdateRequest AuthorUpdateRequest) {
        Author author = getAuthorByIdOrElseThrow(authorId);

        AuthorMapper.INSTANCE.updateAuthorFromAuthorUpdateRequest(AuthorUpdateRequest, author);

        return AuthorMapper.INSTANCE.authorToAuthorDto(authorRepository.save(author));
    }

    @Override
    public List<BookDto> addBookToAuthor(UUID authorId, UUID bookId) {
        Author author = getAuthorByIdOrElseThrow(authorId);
        Book book = bookService.getBookByIdOrElseThrow(bookId);

        bookService.findBookByIdAndAuthorId(bookId, authorId)
            .ifPresent(b -> {
                throw new DataConflictException("Book already exists in author");
            });

        book.getAuthors().add(author);

        authorRepository.save(author);

        return bookService.getAllBooksByAuthorId(authorId);
    }

    @Override
    public void deleteById(UUID id) {
        Author author = getAuthorByIdOrElseThrow(id);
        authorRepository.delete(author);
    }

    @Override
    public void deleteBookFromAuthor(UUID authorId, UUID bookId) {
        Author author = getAuthorByIdOrElseThrow(authorId);
        Book book = bookService.getBookByIdOrElseThrow(bookId);

        bookService.findBookByIdAndAuthorId(bookId, authorId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found in author"));

        book.getAuthors().remove(author);

        authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleteAuthorByAuthorId(UUID authorId) {
        validateAuthorExists(authorId);

        authorRepository.deleteBookAuthorByAuthorId(authorId);
        authorRepository.deleteById(authorId);
    }

    private Author findByIdOrElseThrow(UUID id){
        return authorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
    }

    private void validateAuthorExists(UUID authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Author not found with id: " + authorId);
        }
    }
}
