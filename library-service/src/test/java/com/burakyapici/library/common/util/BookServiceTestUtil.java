package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.BookCopy;
import com.burakyapici.library.domain.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookServiceTestUtil {
    public static List<Book> createSampleBooks(int count) {
        Author author = AuthorServiceTestUtil.createSampleAuthor();
        Genre genre = GenreServiceTestUtil.createSampleGenre();

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Book book = Book.builder()
                        .title("Test Book " + (i + 1))
                        .isbn("978123456789" + i)
                        .bookStatus(BookStatus.ACTIVE)
                        .page(100 + i * 10)
                        .publicationDate(LocalDate.of(2022, 1, Math.min(i + 1, 28)))
                        .authors(Collections.singleton(author))
                        .genres(Collections.singleton(genre))
                        .build();
                    // book.setId(UUID.randomUUID()); // ID should be set via createSampleBookWithId if needed for specific tests
                    return book;
                })
                .collect(Collectors.toList());
    }

    public static Book createSampleBookWithId(UUID bookId) {
        Book book = Book.builder()
            .title("Util Sample Book With ID")
            .isbn(UUID.randomUUID().toString().replace("-", "").substring(0, 13)) // Ensure valid ISBN format
            .bookStatus(BookStatus.ACTIVE)
            .page(222)
            .publicationDate(LocalDate.now().minusMonths(6))
            .authors(new HashSet<>()) 
            .genres(new HashSet<>()) 
            .build();
        book.setId(bookId); // Assuming Book extends BaseModel and has setId
        return book;
    }

    public static BookCopy createSampleBookCopy(Book book) {
        BookCopy bookCopy = BookCopy.builder()
            .book(book)
            .status(BookCopyStatus.AVAILABLE)
            .barcode(UUID.randomUUID().toString().substring(0, 12))
            .build();

        bookCopy.setId(UUID.randomUUID());
        return bookCopy;
    }

    public static List<BookCopy> createSampleBookCopies(Book book, int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> createSampleBookCopy(book))
            .collect(Collectors.toList());
    }

    public static BookCopyDto createSampleBookCopyDto(BookCopy bookCopy) {
        return new BookCopyDto(
            bookCopy.getId(),
            bookCopy.getBarcode(),
            bookCopy.getStatus()
        );
    }

    public static List<BookCopyDto> createSampleBookCopyDtos(List<BookCopy> bookCopies) {
        return bookCopies.stream()
            .map(BookServiceTestUtil::createSampleBookCopyDto)
            .collect(Collectors.toList());
    }

    public static BookCreateRequest createSampleBookCreateRequest(Set<UUID> authorIds, Set<UUID> genreIds) {
        return new BookCreateRequest(
            "New Test Book",
            "9780987654321",
            BookStatus.ACTIVE,
            250,
            LocalDate.now(),
            authorIds,
            genreIds
        );
    }

    public static BookCreateRequest createSampleBookCreateRequest() {
        Set<Author> authors = AuthorServiceTestUtil.createSampleAuthors(1);
        Set<UUID> authorIds = authors.stream()
            .map(Author::getId)
            .collect(Collectors.toSet());

        Set<Genre> genres = GenreServiceTestUtil.createSampleGenres(1);
        Set<UUID> genreIds = genres.stream()
            .map(Genre::getId)
            .collect(Collectors.toSet());

        return createSampleBookCreateRequest(authorIds, genreIds);
    }

    public static BookUpdateRequest createSampleBookUpdateRequest(Set<UUID> authorIds, Set<UUID> genreIds) {
        return new BookUpdateRequest(
            "Updated Test Book",
            BookStatus.ARCHIVED,
            300,
            LocalDate.now().minusYears(1),
            authorIds,
            genreIds
        );
    }

    public static BookUpdateRequest createSampleBookUpdateRequest() {
        Set<Author> authors = AuthorServiceTestUtil.createSampleAuthors(1);
        Set<UUID> authorIds = authors.stream()
            .map(Author::getId)
            .collect(Collectors.toSet());

        Set<Genre> genres = GenreServiceTestUtil.createSampleGenres(1);
        Set<UUID> genreIds = genres.stream()
            .map(Genre::getId)
            .collect(Collectors.toSet());

        return createSampleBookUpdateRequest(authorIds, genreIds);
    }

    public static BookSearchCriteria createSampleBookSearchCriteria(
        String title, 
        String isbn, 
        BookStatus bookStatus, 
        Integer page, 
        LocalDate publicationDate, 
        UUID genreId, 
        UUID authorId
    ) {
        return new BookSearchCriteria(title, isbn, bookStatus, page, publicationDate, genreId, authorId);
    }

    public static BookSearchCriteria createEmptyBookSearchCriteria() {
        return new BookSearchCriteria(null, null, null, null, null, null, null);
    }

    public static Page<Book> createBookPage(List<Book> books, int currentPage, int pageSize, long totalElements) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return new PageImpl<>(books, pageable, totalElements);
    }

    public static Page<Book> createEmptyBookPage(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}