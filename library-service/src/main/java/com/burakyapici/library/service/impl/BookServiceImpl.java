package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.BookRepository;
import com.burakyapici.library.domain.specification.BookSpecifications;
import com.burakyapici.library.service.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final WaitListService waitListService;
    private final BookCopyService bookCopyService;
    private final BorrowingService borrowingService;

    public BookServiceImpl(
        BookRepository bookRepository,
        @Lazy
        AuthorService authorService,
        GenreService genreService,
        WaitListService waitListService,
        BookCopyService bookCopyService,
        BorrowingService borrowingService
    ) {
        this.waitListService = waitListService;
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
        this.bookCopyService = bookCopyService;
        this.borrowingService = borrowingService;
    }

    @Override
    public PageableDto<BookDto> getAllBooks(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Book> allBooksPage = bookRepository.findAll(pageable);

        return createPageableResponse(allBooksPage, pageSize);
    }

    @Override
    public List<BookDto> getAllBooksByAuthorId(UUID authorId) {
        return BookMapper.INSTANCE.toBookDtoList(
            bookRepository.findAllByAuthors_Id(authorId)
        );
    }

    @Override
    public BookDetailDto getBookDetailById(UUID id) {
        Book book = getBookByIdOrElseThrow(id);
        return BookMapper.INSTANCE.toBookDetailDto(book);
    }

    @Override
    public Book getBookByIdOrElseThrow(UUID id) {
        return findById(id);
    }

    @Override
    public PageableDto<BookCopyDto> getBookCopiesById(UUID id, int currentPage, int pageSize) {
        validateBookExistsById(id);
        return bookCopyService.getAllBookCopiesByBookId(id, currentPage, pageSize);
    }

    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest) {
        validateUniqueIsbn(bookCreateRequest.isbn());

        Set<Author> authors = authorService.getAuthorsByIdsOrElseThrow(bookCreateRequest.authorIds());
        Set<Genre> genres = genreService.getGenresByIdsOrElseThrow(bookCreateRequest.genreIds());

        Book book = Book.builder()
            .title(bookCreateRequest.title())
            .isbn(bookCreateRequest.isbn())
            .bookStatus(bookCreateRequest.bookStatus())
            .page(bookCreateRequest.page())
            .publicationDate(bookCreateRequest.publicationDate())
            .authors(Set.of(authors.toArray(Author[]::new)))
            .genres(Set.of(genres.toArray(Genre[]::new)))
        .build();

        bookRepository.save(book);

        return BookMapper.INSTANCE.toBookDto(book);
    }

    @Override
    public BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest){
        Book book = getBookByIdOrElseThrow(id);

        BookMapper.INSTANCE.updateBookFromBookUpdateRequest(
            bookUpdateRequest,
            book,
            authorService,
            genreService,
            waitListService
        );

        return BookMapper.INSTANCE.toBookDto(bookRepository.save(book));
    }

    @Override
    public PageableDto<BookDto> searchBooks(BookSearchCriteria bookSearchCriteria, int currentPage, int pageSize) {
        Specification<Book> spec = BookSpecifications.findByCriteria(bookSearchCriteria);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Book> allBooksPage = bookRepository.findAll(spec, pageable);

        return createPageableResponse(allBooksPage, pageSize);
    }

    @Override
    public Optional<Book> findBookByIdAndAuthorId(UUID bookId, UUID authorId) {
        return bookRepository.findByIdAndAuthors_Id(bookId, authorId);
    }

    @Override
    public int calculateAvailableCopiesCount(UUID bookId) {
        validateBookExistsById(bookId);
        return bookCopyService.countByIdAndStatus(bookId, BookCopyStatus.AVAILABLE);
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        validateBookExistsById(id);
        waitListService.deleteByBookId(id);
        borrowingService.deleteAllByBookId(id);
        bookCopyService.deleteAllByBookId(id);
        bookRepository.deleteById(id);
    }

    private Book findById(UUID id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
    }

    private void validateUniqueIsbn(String isbn) {
        if(bookRepository.existsByIsbn(isbn)) {
            throw new DataConflictException("Book with the same ISBN already exists.");
        }
    }

    private void validateBookExistsById(UUID id) {
        if(!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
    }

    private PageableDto<BookDto> createPageableResponse(Page<Book> bookPage, int pageSize) {
        List<BookDto> bookDtoList = BookMapper.INSTANCE.toBookDtoList(bookPage.getContent());

        return new PageableDto<>(
            bookDtoList,
            bookPage.getTotalPages(),
            pageSize,
            bookPage.getNumber(),
            bookPage.hasNext(),
            bookPage.hasPrevious()
        );
    }
}
