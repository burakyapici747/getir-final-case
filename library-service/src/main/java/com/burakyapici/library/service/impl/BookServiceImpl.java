package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.common.mapper.BookMapper;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.BookRepository;
import com.burakyapici.library.exception.BookNotFoundException;
import com.burakyapici.library.service.AuthorService;
import com.burakyapici.library.service.BookService;
import com.burakyapici.library.service.GenreService;
import com.burakyapici.library.service.WaitListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService {
    private static final int TOTAL_ELEMENTS_PER_PAGE = 10;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final WaitListService waitListService;

    public BookServiceImpl(
        BookRepository bookRepository,
        AuthorService authorService,
        GenreService genreService,
        WaitListService waitListService
    ) {
        this.waitListService = waitListService;
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @Override
    public PageableDto<BookDto> getAllBooks(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Book> allBooksPage = bookRepository.findAll(pageable);
        List<BookDto> bookDtoList = BookMapper.INSTANCE.bookListToBookDtoList(allBooksPage.getContent());

        return new PageableDto<>(
            bookDtoList,
            allBooksPage.getTotalPages(),
            TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            allBooksPage.hasNext(),
            allBooksPage.hasPrevious()
        );
    }

    @Override
    public Book getBookByIdOrElseThrow(UUID id) {
        return findById(id);
    }

    @Override
    public BookDto createBook(BookCreateRequest bookCreateRequest) {
        validateBookForCreation(bookCreateRequest);

        Set<Author> authors = authorService.getAuthorsByIdsOrElseThrow(bookCreateRequest.authorIds());
        Set<Genre> genres = genreService.getGenresByIdsOrElseThrow(bookCreateRequest.genreIds());

        Book book = Book.builder()
            .title(bookCreateRequest.title())
            .isbn(bookCreateRequest.isbn())
            .bookStatus(bookCreateRequest.bookStatus())
            .page(bookCreateRequest.page())
            .publicationDate(bookCreateRequest.publicationDate())
            .author(Set.of(authors.toArray(Author[]::new)))
            .genres(Set.of(genres.toArray(Genre[]::new)))
        .build();

        bookRepository.save(book);

        return BookMapper.INSTANCE.bookToBookDto(book);
    }

    @Override
    public BookDetailDto getBookDetailById(UUID id) {
        Book book = getBookByIdOrElseThrow(id);
        return BookMapper.INSTANCE.bookToBookDetailDto(book);
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

        return BookMapper.INSTANCE.bookToBookDto(book);
    }

    @Override
    public void deleteBook(UUID id) {
        Book book = getBookByIdOrElseThrow(id);
        bookRepository.delete(book);
    }

    private Book findById(UUID id) {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + id));
    }

    private void validateBookForCreation(BookCreateRequest bookCreateRequest) {
        if (bookRepository.existsByIsbn(bookCreateRequest.isbn())) {
            throw new RuntimeException("Book with ISBN " + bookCreateRequest.isbn() + " already exists.");
        }

        if(bookRepository.existsByTitle(bookCreateRequest.title())) {
            throw new RuntimeException("Book with title " + bookCreateRequest.title() + " already exists.");
        }
    }
}
