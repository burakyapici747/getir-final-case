package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookSearchCriteria;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.common.util.AuthorServiceTestUtil;
import com.burakyapici.library.common.util.BookServiceTestUtil;
import com.burakyapici.library.common.util.GenreServiceTestUtil;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.model.Genre;
import com.burakyapici.library.domain.repository.BookRepository;
import com.burakyapici.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @Mock
    private WaitListService waitListService;

    @Mock
    private BookCopyService bookCopyService;

    @Mock
    private BorrowingService borrowingService;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Should return pageable book DTOs when books exist")
    void givenExistingBooks_whenGetAllBooks_thenReturnPageableDto() {
        int currentPage = 0;
        int pageSize = 10;
        int totalBooks = 2;
        
        List<Book> books = BookServiceTestUtil.createSampleBooks(totalBooks);
        Page<Book> bookPage = BookServiceTestUtil.createBookPage(books, currentPage, pageSize, totalBooks);
        
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        
        PageableDto<BookDto> result = bookService.getAllBooks(currentPage, pageSize);
        
        assertNotNull(result);
        assertEquals(totalBooks, result.elements().size());
        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage());
        assertEquals(1, result.totalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty pageable when no books are found")
    void givenNoBooks_whenGetAllBooks_thenReturnEmptyPageableDto() {
        int currentPage = 0;
        int pageSize = 10;
        
        Page<Book> emptyPage = BookServiceTestUtil.createEmptyBookPage(currentPage, pageSize);
        
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
        
        PageableDto<BookDto> result = bookService.getAllBooks(currentPage, pageSize);
        
        assertNotNull(result);
        assertTrue(result.elements().isEmpty());
        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage());
        assertEquals(0, result.totalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(bookRepository, times(1)).findAll(pageable);
    }
    
    @Test
    @DisplayName("Should return pageable with proper pagination data")
    void givenMultiplePages_whenGetAllBooks_thenReturnPageableWithPaginationData() {
        int currentPage = 1;
        int pageSize = 1;
        int totalBooks = 3;
        int booksOnCurrentPage = 1;
        
        List<Book> books = BookServiceTestUtil.createSampleBooks(booksOnCurrentPage);
        Page<Book> secondPage = BookServiceTestUtil.createBookPage(books, currentPage, pageSize, totalBooks);
        
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(secondPage);
        
        PageableDto<BookDto> result = bookService.getAllBooks(currentPage, pageSize);
        
        assertNotNull(result);
        assertEquals(booksOnCurrentPage, result.elements().size());
        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage());
        assertEquals(3, result.totalPages());
        assertTrue(result.hasNext());
        assertTrue(result.hasPrevious());
        
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return books for a given author ID when books exist")
    void givenAuthorWithBooks_whenGetAllBooksByAuthorId_thenReturnBookDtoList() {
        UUID authorId = UUID.randomUUID();
        int numberOfBooks = 2;
        List<Book> mockBooks = BookServiceTestUtil.createSampleBooks(numberOfBooks);

        when(bookRepository.findAllByAuthors_Id(authorId)).thenReturn(mockBooks);

        List<BookDto> result = bookService.getAllBooksByAuthorId(authorId);

        assertNotNull(result);
        assertEquals(numberOfBooks, result.size());

        verify(bookRepository, times(1)).findAllByAuthors_Id(authorId);
    }

    @Test
    @DisplayName("Should return an empty list for a given author ID when no books exist")
    void givenAuthorWithNoBooks_whenGetAllBooksByAuthorId_thenReturnEmptyList() {
        UUID authorId = UUID.randomUUID();
        when(bookRepository.findAllByAuthors_Id(authorId)).thenReturn(Collections.emptyList());

        List<BookDto> result = bookService.getAllBooksByAuthorId(authorId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository, times(1)).findAllByAuthors_Id(authorId);
    }

    @Test
    @DisplayName("Should return book detail DTO when book exists")
    void givenExistingBookId_whenGetBookDetailById_thenReturnBookDetailDto() {
        UUID bookId = UUID.randomUUID();
        Book mockBook = BookServiceTestUtil.createSampleBooks(1).getFirst();
        mockBook.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        BookDetailDto result = bookService.getBookDetailById(bookId);

        assertNotNull(result);
        assertEquals(mockBook.getTitle(), result.title());

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when book does not exist")
    void givenNonExistingBookId_whenGetBookDetailById_thenThrowEntityNotFoundException() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookService.getBookDetailById(bookId));

        assertEquals("Book not found with id: " + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("Should return pageable book copy DTOs when book exists")
    void givenExistingBookId_whenGetBookCopiesById_thenReturnPageableBookCopyDto() {
        UUID bookId = UUID.randomUUID();
        int currentPage = 0;
        int pageSize = 5;
        int totalCopies = 3;
        
        Book dummyBook = BookServiceTestUtil.createSampleBooks(1).getFirst();
        List<com.burakyapici.library.domain.model.BookCopy> sampleCopies = 
                BookServiceTestUtil.createSampleBookCopies(dummyBook, totalCopies);
        List<BookCopyDto> sampleCopyDtoList = BookServiceTestUtil.createSampleBookCopyDtos(sampleCopies);

        PageableDto<BookCopyDto> mockPageableDto = new PageableDto<>(
            sampleCopyDtoList,
            1, 
            totalCopies, 
            currentPage, 
            false, 
            false
        );

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookCopyService.getAllBookCopiesByBookId(bookId, currentPage, pageSize)).thenReturn(mockPageableDto);

        PageableDto<BookCopyDto> result = bookService.getBookCopiesById(bookId, currentPage, pageSize);

        assertNotNull(result);
        assertEquals(totalCopies, result.elements().size());
        assertEquals(currentPage, result.currentPage());

        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCopyService, times(1)).getAllBookCopiesByBookId(bookId, currentPage, pageSize);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting copies for non-existing book")
    void givenNonExistingBookId_whenGetBookCopiesById_thenThrowEntityNotFoundException() {
        UUID bookId = UUID.randomUUID();
        int currentPage = 0;
        int pageSize = 5;

        when(bookRepository.existsById(bookId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class, () -> bookService.getBookCopiesById(bookId, currentPage, pageSize)
        );

        assertEquals("Book not found with id: " + bookId, exception.getMessage());

        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCopyService, never()).getAllBookCopiesByBookId(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should create book and return DTO when request is valid")
    void givenValidBookCreateRequest_whenCreateBook_thenReturnBookDto() {
        Set<Author> authors = AuthorServiceTestUtil.createSampleAuthors(1);
        Set<Genre> genres = GenreServiceTestUtil.createSampleGenres(1);
        Set<UUID> authorIds = authors.stream().map(Author::getId).collect(Collectors.toSet());
        Set<UUID> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toSet());
        BookCreateRequest request = BookServiceTestUtil.createSampleBookCreateRequest(authorIds, genreIds);

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
        when(authorService.getAuthorsByIdsOrElseThrow(authorIds)).thenReturn(authors);
        when(genreService.getGenresByIdsOrElseThrow(genreIds)).thenReturn(genres);
        when(bookRepository.save(any(Book.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );

        BookDto result = bookService.createBook(request);

        assertNotNull(result);
        assertEquals(request.title(), result.title());
        assertEquals(request.isbn(), result.isbn());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        assertEquals(request.title(), bookCaptor.getValue().getTitle());
        assertEquals(request.isbn(), bookCaptor.getValue().getIsbn());
        assertEquals(authors.size(), bookCaptor.getValue().getAuthors().size());
        assertEquals(genres.size(), bookCaptor.getValue().getGenres().size());

        verify(bookRepository, times(1)).existsByIsbn(request.isbn());
        verify(authorService, times(1)).getAuthorsByIdsOrElseThrow(authorIds);
        verify(genreService, times(1)).getGenresByIdsOrElseThrow(genreIds);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw DataConflictException when ISBN already exists")
    void givenExistingIsbn_whenCreateBook_thenThrowDataConflictException() {
        BookCreateRequest request = BookServiceTestUtil.createSampleBookCreateRequest();

        when(bookRepository.existsByIsbn(request.isbn())).thenReturn(true);

        DataConflictException exception = assertThrows(DataConflictException.class, () -> bookService.createBook(request));

        assertEquals("Book with the same ISBN already exists.", exception.getMessage());

        verify(bookRepository, times(1)).existsByIsbn(request.isbn());
        verify(authorService, never()).getAuthorsByIdsOrElseThrow(any());
        verify(genreService, never()).getGenresByIdsOrElseThrow(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when author ID is invalid during creation")
    void givenInvalidAuthorId_whenCreateBook_thenThrowException() {
         Set<UUID> invalidAuthorIds = Collections.singleton(UUID.randomUUID());
         Set<UUID> validGenreIds = GenreServiceTestUtil.createSampleGenres(1).stream().map(Genre::getId).collect(Collectors.toSet());
         BookCreateRequest request = BookServiceTestUtil.createSampleBookCreateRequest(invalidAuthorIds, validGenreIds);
         RuntimeException expectedException = new RuntimeException("Author not found");

         when(bookRepository.existsByIsbn(request.isbn())).thenReturn(false);
         when(authorService.getAuthorsByIdsOrElseThrow(invalidAuthorIds)).thenThrow(expectedException);

         RuntimeException actualException = assertThrows(RuntimeException.class, () -> bookService.createBook(request));

         assertEquals(expectedException.getMessage(), actualException.getMessage());

         verify(bookRepository, times(1)).existsByIsbn(request.isbn());
         verify(authorService, times(1)).getAuthorsByIdsOrElseThrow(invalidAuthorIds);
         verify(genreService, never()).getGenresByIdsOrElseThrow(any());
         verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update book and return DTO when request is valid")
    void givenValidBookUpdateRequestAndId_whenUpdateBook_thenReturnUpdatedBookDto() {
        UUID bookId = UUID.randomUUID();
        Book existingBook = BookServiceTestUtil.createSampleBooks(1).getFirst();
        existingBook.setId(bookId);
        
        Set<Author> updatedAuthors = AuthorServiceTestUtil.createSampleAuthors(1);
        Set<Genre> updatedGenres = GenreServiceTestUtil.createSampleGenres(1);
        Set<UUID> updatedAuthorIds = updatedAuthors.stream().map(Author::getId).collect(Collectors.toSet());
        Set<UUID> updatedGenreIds = updatedGenres.stream().map(Genre::getId).collect(Collectors.toSet());
        BookUpdateRequest request = BookServiceTestUtil.createSampleBookUpdateRequest(updatedAuthorIds, updatedGenreIds);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorService.getAuthorsByIdsOrElseThrow(updatedAuthorIds)).thenReturn(updatedAuthors);
        when(genreService.getGenresByIdsOrElseThrow(updatedGenreIds)).thenReturn(updatedGenres);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookDto result = bookService.updateBook(bookId, request);

        assertNotNull(result);
        assertEquals(request.title(), result.title());

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        assertEquals(bookId, bookCaptor.getValue().getId());
        assertEquals(request.title(), bookCaptor.getValue().getTitle());
        assertEquals(request.bookStatus(), bookCaptor.getValue().getBookStatus());
        assertEquals(updatedAuthors.size(), bookCaptor.getValue().getAuthors().size());

        verify(bookRepository, times(1)).findById(bookId);
        verify(authorService, times(1)).getAuthorsByIdsOrElseThrow(updatedAuthorIds);
        verify(genreService, times(1)).getGenresByIdsOrElseThrow(updatedGenreIds);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existing book")
    void givenNonExistingBookId_whenUpdateBook_thenThrowEntityNotFoundException() {
        UUID bookId = UUID.randomUUID();
        BookUpdateRequest request = BookServiceTestUtil.createSampleBookUpdateRequest();

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class, () -> bookService.updateBook(bookId, request)
        );

        assertEquals("Book not found with id: " + bookId, exception.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when author ID is invalid during update")
    void givenInvalidAuthorIdInUpdate_whenUpdateBook_thenThrowException() {
        UUID bookId = UUID.randomUUID();
        Book existingBook = BookServiceTestUtil.createSampleBooks(1).getFirst();
        existingBook.setId(bookId);
        
        Set<UUID> invalidAuthorIds = Collections.singleton(UUID.randomUUID());
        Set<UUID> validGenreIds = GenreServiceTestUtil.createSampleGenres(1).stream().map(Genre::getId).collect(Collectors.toSet());
        BookUpdateRequest request = BookServiceTestUtil.createSampleBookUpdateRequest(invalidAuthorIds, validGenreIds);
        RuntimeException expectedException = new RuntimeException("Author not found");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(authorService.getAuthorsByIdsOrElseThrow(invalidAuthorIds)).thenThrow(expectedException);

        RuntimeException actualException = assertThrows(
            RuntimeException.class, () -> bookService.updateBook(bookId, request)
        );

        assertEquals(expectedException.getMessage(), actualException.getMessage());

        verify(bookRepository, times(1)).findById(bookId);
        verify(authorService, times(1)).getAuthorsByIdsOrElseThrow(invalidAuthorIds);
        verify(genreService, never()).getGenresByIdsOrElseThrow(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return pageable book DTOs matching the search criteria")
    void givenValidCriteria_whenSearchBooks_thenReturnMatchingBooksPage() {
        int currentPage = 0;
        int pageSize = 10;
        int totalMatchingBooks = 1;

        BookSearchCriteria criteria = BookServiceTestUtil.createSampleBookSearchCriteria(
            "Test Book",
            null,
            null,
            null,
            null,
            null,
            null
        );

        List<Book> matchingBooks = BookServiceTestUtil.createSampleBooks(totalMatchingBooks);
        Page<Book> bookPage = BookServiceTestUtil.createBookPage(matchingBooks, currentPage, pageSize, totalMatchingBooks);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookPage);

        PageableDto<BookDto> result = bookService.searchBooks(criteria, currentPage, pageSize);

        assertNotNull(result);
        assertEquals(totalMatchingBooks, result.elements().size());
        assertEquals(currentPage, result.currentPage());
        
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty pageable when search criteria matches no books")
    void givenCriteriaWithNoMatches_whenSearchBooks_thenReturnEmptyPage() {
        int currentPage = 0;
        int pageSize = 10;
        BookSearchCriteria criteria = BookServiceTestUtil.createSampleBookSearchCriteria(
            "NonExistentTitle",
            null,
            null,
            null,
            null,
            null,
            null
        );

        Page<Book> emptyPage = BookServiceTestUtil.createEmptyBookPage(currentPage, pageSize);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        PageableDto<BookDto> result = bookService.searchBooks(criteria, currentPage, pageSize);

        assertNotNull(result);
        assertTrue(result.elements().isEmpty());
        assertEquals(0, result.totalPages());
        
        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return Optional of book when book and author IDs match")
    void givenMatchingBookAndAuthorId_whenFindBookByIdAndAuthorId_thenReturnOptionalBook() {
        UUID bookId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Book mockBook = BookServiceTestUtil.createSampleBooks(1).getFirst();
        
        when(bookRepository.findByIdAndAuthors_Id(bookId, authorId)).thenReturn(Optional.of(mockBook));

        Optional<Book> result = bookService.findBookByIdAndAuthorId(bookId, authorId);

        assertTrue(result.isPresent());
        assertEquals(mockBook, result.get());
        
        verify(bookRepository, times(1)).findByIdAndAuthors_Id(bookId, authorId);
    }

    @Test
    @DisplayName("Should return empty Optional when book and author IDs do not match")
    void givenNonMatchingIds_whenFindBookByIdAndAuthorId_thenReturnEmptyOptional() {
        UUID bookId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        
        when(bookRepository.findByIdAndAuthors_Id(bookId, authorId)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findBookByIdAndAuthorId(bookId, authorId);

        assertFalse(result.isPresent());
        
        verify(bookRepository, times(1)).findByIdAndAuthors_Id(bookId, authorId);
    }

    @Test
    @DisplayName("Should return count of available copies from BookCopyService")
    void givenBookId_whenCalculateAvailableCopiesCount_thenReturnCountFromBookCopyService() {
        UUID bookId = UUID.randomUUID();
        int expectedCount = 5;
        
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(bookCopyService.countByIdAndStatus(bookId, BookCopyStatus.AVAILABLE)).thenReturn(expectedCount);

        int actualCount = bookService.calculateAvailableCopiesCount(bookId);

        assertEquals(expectedCount, actualCount);
        
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCopyService, times(1)).countByIdAndStatus(bookId, BookCopyStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when calculating copies for non-existing book")
    void givenNonExistingBookId_whenCalculateAvailableCopiesCount_thenThrowEntityNotFoundException() {
        UUID bookId = UUID.randomUUID();
        
        when(bookRepository.existsById(bookId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> bookService.calculateAvailableCopiesCount(bookId)
        );

        assertEquals("Book not found with id: " + bookId, exception.getMessage());
        
        verify(bookRepository, times(1)).existsById(bookId);
        verify(bookCopyService, never()).countByIdAndStatus(any(), any());
    }

    @Test
    @DisplayName("Should save and return the book")
    void givenBookToSave_whenSaveBook_thenReturnSavedBook() {
        Book bookToSave = BookServiceTestUtil.createSampleBooks(1).getFirst();
        
        when(bookRepository.save(any(Book.class))).thenReturn(bookToSave);

        Book savedBook = bookService.saveBook(bookToSave);

        assertNotNull(savedBook);
        assertEquals(bookToSave, savedBook);
        
        verify(bookRepository, times(1)).save(bookToSave);
    }

    @Test
    @DisplayName("Should call dependent delete methods and repository delete when book exists")
    void givenExistingBookId_whenDeleteById_thenCallDependentDeleteMethodsAndRepositoryDelete() {
        UUID bookId = UUID.randomUUID();
        
        when(bookRepository.existsById(bookId)).thenReturn(true);
        doNothing().when(waitListService).deleteByBookId(bookId);
        doNothing().when(borrowingService).deleteAllByBookId(bookId);
        doNothing().when(bookCopyService).deleteAllByBookId(bookId);
        doNothing().when(bookRepository).deleteById(bookId);

        assertDoesNotThrow(() -> bookService.deleteById(bookId));
        
        InOrder inOrder = inOrder(bookRepository, waitListService, borrowingService, bookCopyService);
        inOrder.verify(bookRepository).existsById(bookId);
        inOrder.verify(waitListService).deleteByBookId(bookId);
        inOrder.verify(borrowingService).deleteAllByBookId(bookId);
        inOrder.verify(bookCopyService).deleteAllByBookId(bookId);
        inOrder.verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existing book")
    void givenNonExistingBookId_whenDeleteById_thenThrowEntityNotFoundException() {
        UUID bookId = UUID.randomUUID();
        
        when(bookRepository.existsById(bookId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> bookService.deleteById(bookId)
        );

        assertEquals("Book not found with id: " + bookId, exception.getMessage());
        
        verify(bookRepository, times(1)).existsById(bookId);
        verify(waitListService, never()).deleteByBookId(any());
        verify(borrowingService, never()).deleteAllByBookId(any());
        verify(bookCopyService, never()).deleteAllByBookId(any());
        verify(bookRepository, never()).deleteById(any());
    }
} 