package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.common.util.AuthorServiceTestUtil;
import com.burakyapici.library.common.util.BookServiceTestUtil;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.model.Author;
import com.burakyapici.library.domain.model.Book;
import com.burakyapici.library.domain.repository.AuthorRepository;
import com.burakyapici.library.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author sampleAuthor;
    private AuthorDto sampleAuthorDto;
    private UUID authorId;
    private Book sampleBook;
    private BookDto sampleBookDto;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        sampleAuthor = AuthorServiceTestUtil.createSampleAuthorWithId(authorId); 
        sampleAuthorDto = new AuthorDto(authorId, sampleAuthor.getFirstName(), sampleAuthor.getLastName(), sampleAuthor.getDateOfBirth());

        bookId = UUID.randomUUID();
        sampleBook = BookServiceTestUtil.createSampleBookWithId(bookId); 
        sampleBookDto = new BookDto(bookId.toString(), sampleBook.getTitle(), sampleBook.getIsbn(), sampleBook.getBookStatus(), sampleBook.getPublicationDate(), sampleBook.getPage(), Collections.emptyList(), Collections.emptyList());
    }

    @Test
    @DisplayName("getAllAuthors - Should return pageable author DTOs")
    void getAllAuthors_shouldReturnPageableAuthorDtos() {
        int currentPage = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        List<Author> authors = List.of(sampleAuthor);
        Page<Author> authorPage = new PageImpl<>(authors, pageable, authors.size());

        when(authorRepository.findAll(pageable)).thenReturn(authorPage);

        PageableDto<AuthorDto> result = authorService.getAllAuthors(currentPage, pageSize);

        assertNotNull(result);
        assertEquals(1, result.elements().size());
        assertEquals(sampleAuthor.getId(), ((List<AuthorDto>)result.elements()).getFirst().id());
        assertEquals(sampleAuthor.getFirstName(), ((List<AuthorDto>)result.elements()).getFirst().firstName());
        assertEquals(currentPage, result.currentPage());
        assertEquals(1, result.totalPages()); 
        assertEquals(10, result.totalElementsPerPage()); 
        verify(authorRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("getAllAuthors - Should return empty pageable DTO when no authors")
    void getAllAuthors_whenNoAuthors_shouldReturnEmptyPageableDto() {
        int currentPage = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Author> emptyAuthorPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(authorRepository.findAll(pageable)).thenReturn(emptyAuthorPage);

        PageableDto<AuthorDto> result = authorService.getAllAuthors(currentPage, pageSize);

        assertNotNull(result);
        assertTrue(result.elements().isEmpty());
        assertEquals(0, result.totalPages());
        verify(authorRepository).findAll(pageable);
    }

    @Test
    @DisplayName("searchAuthors - Should return pageable author DTOs based on criteria")
    void searchAuthors_shouldReturnPageableAuthorDtos() {
        int currentPage = 0;
        int pageSize = 5;
        AuthorSearchCriteria criteria = new AuthorSearchCriteria("Test", null, null);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        List<Author> authors = List.of(sampleAuthor);
        Page<Author> authorPage = new PageImpl<>(authors, pageable, authors.size());

        when(authorRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(authorPage);

        PageableDto<AuthorDto> result = authorService.searchAuthors(criteria, currentPage, pageSize);

        assertNotNull(result);
        assertEquals(1, result.elements().size());
        assertEquals(sampleAuthor.getId(), ((List<AuthorDto>)result.elements()).getFirst().id());
        assertEquals(currentPage, result.currentPage());
        assertEquals(pageSize, result.totalElementsPerPage());
        verify(authorRepository).findAll(any(Specification.class), eq(pageable));
    }
    
    @Test
    @DisplayName("searchAuthors - Should return empty pageable DTO when no authors match criteria")
    void searchAuthors_whenNoAuthorsMatch_shouldReturnEmptyPageableDto() {
        int currentPage = 0;
        int pageSize = 5;
        AuthorSearchCriteria criteria = new AuthorSearchCriteria("NonExistent", null, null);
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Author> emptyAuthorPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(authorRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyAuthorPage);

        PageableDto<AuthorDto> result = authorService.searchAuthors(criteria, currentPage, pageSize);

        assertNotNull(result);
        assertTrue(result.elements().isEmpty());
        assertEquals(0, result.totalPages());
        verify(authorRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("getAuthorById - Should return author DTO when found")
    void getAuthorById_whenFound_shouldReturnAuthorDto() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));

        AuthorDto result = authorService.getAuthorById(authorId);

        assertNotNull(result);
        assertEquals(sampleAuthor.getId(), result.id());
        assertEquals(sampleAuthor.getFirstName(), result.firstName());
        verify(authorRepository).findById(authorId);
    }

    @Test
    @DisplayName("getAuthorById - Should throw EntityNotFoundException when not found")
    void getAuthorById_whenNotFound_shouldThrowEntityNotFoundException() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.getAuthorById(authorId));
        verify(authorRepository).findById(authorId);
    }

    @Test
    @DisplayName("getAuthorByIdOrElseThrow - Should return author entity when found")
    void getAuthorByIdOrElseThrow_whenFound_shouldReturnAuthorEntity() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));

        Author result = authorService.getAuthorByIdOrElseThrow(authorId);

        assertNotNull(result);
        assertEquals(sampleAuthor, result);
        verify(authorRepository).findById(authorId);
    }

    @Test
    @DisplayName("getAuthorByIdOrElseThrow - Should throw EntityNotFoundException when not found")
    void getAuthorByIdOrElseThrow_whenNotFound_shouldThrowEntityNotFoundException() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.getAuthorByIdOrElseThrow(authorId));
        verify(authorRepository).findById(authorId);
    }

    @Test
    @DisplayName("getAuthorsByIdsOrElseThrow - Should return set of authors when all found")
    void getAuthorsByIdsOrElseThrow_whenAllFound_shouldReturnSetOfAuthors() {
        Set<UUID> authorIdsSet = Set.of(authorId);
        List<Author> authorsList = List.of(sampleAuthor);
        when(authorRepository.findAllById(authorIdsSet)).thenReturn(authorsList);

        Set<Author> result = authorService.getAuthorsByIdsOrElseThrow(authorIdsSet);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(sampleAuthor));
        verify(authorRepository).findAllById(authorIdsSet);
    }

    @Test
    @DisplayName("getAuthorsByIdsOrElseThrow - Should throw EntityNotFoundException when some not found")
    void getAuthorsByIdsOrElseThrow_whenSomeNotFound_shouldThrowEntityNotFoundException() {
        UUID foundId = authorId;
        UUID notFoundId = UUID.randomUUID();
        Set<UUID> authorIdsSet = Set.of(foundId, notFoundId);
        List<Author> authorsList = List.of(sampleAuthor); 

        when(authorRepository.findAllById(authorIdsSet)).thenReturn(authorsList);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> authorService.getAuthorsByIdsOrElseThrow(authorIdsSet));
        
        assertTrue(exception.getMessage().contains(notFoundId.toString()));
        verify(authorRepository).findAllById(authorIdsSet);
    }
    
    @Test
    @DisplayName("getAuthorsByIdsOrElseThrow - Should throw EntityNotFoundException when no IDs found")
    void getAuthorsByIdsOrElseThrow_whenNoneFound_shouldThrowEntityNotFoundException() {
        Set<UUID> authorIdsSet = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(authorRepository.findAllById(authorIdsSet)).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () -> authorService.getAuthorsByIdsOrElseThrow(authorIdsSet));
        verify(authorRepository).findAllById(authorIdsSet);
    }

    @Test
    @DisplayName("createAuthor - Should create and return author DTO")
    void createAuthor_shouldCreateAndReturnAuthorDto() {
        AuthorCreateRequest createRequest = new AuthorCreateRequest("New", "Author", LocalDate.of(1990, 5, 15));

        Author savedAuthor = Author.builder()
            .firstName(createRequest.firstName())
            .lastName(createRequest.lastName())
            .dateOfBirth(createRequest.dateOfBirth())
            .build();
        savedAuthor.setId(UUID.randomUUID()); 

        ArgumentCaptor<Author> authorToSaveCaptor = ArgumentCaptor.forClass(Author.class);
        when(authorRepository.save(authorToSaveCaptor.capture())).thenReturn(savedAuthor);
        
        AuthorDto result = authorService.createAuthor(createRequest);

        assertNotNull(result);
        assertEquals(savedAuthor.getId(), result.id());
        assertEquals(savedAuthor.getFirstName(), result.firstName());

        Author capturedAuthorToSave = authorToSaveCaptor.getValue();
        assertNull(capturedAuthorToSave.getId()); 
        assertEquals(createRequest.firstName(), capturedAuthorToSave.getFirstName());
        assertEquals(createRequest.lastName(), capturedAuthorToSave.getLastName());
        assertEquals(createRequest.dateOfBirth(), capturedAuthorToSave.getDateOfBirth());
    }

    @Test
    @DisplayName("updateAuthor - Should update and return author DTO")
    void updateAuthor_shouldUpdateAndReturnAuthorDto() {
        String updatedFirstName = "Updated";
        String updatedLastName = "Name";
        LocalDate updatedDob = LocalDate.of(1985, 3, 20);
        AuthorUpdateRequest updateRequest = new AuthorUpdateRequest(updatedFirstName, updatedLastName, updatedDob);
        
        Author existingAuthor = AuthorServiceTestUtil.createSampleAuthorWithId(authorId);
        existingAuthor.setFirstName("OriginalFirstName"); 

        Author authorAfterMappingAndSaving = Author.builder()
            .firstName(updatedFirstName)
            .lastName(updatedLastName)
            .dateOfBirth(updatedDob)
            .build();

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(authorAfterMappingAndSaving);

        AuthorDto result = authorService.updateAuthor(authorId, updateRequest);

        assertNotNull(result);
        assertEquals(authorAfterMappingAndSaving.getId(), result.id());
        assertEquals(authorAfterMappingAndSaving.getFirstName(), result.firstName());

        ArgumentCaptor<Author> authorCaptor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository).save(authorCaptor.capture());
        assertEquals(updatedFirstName, authorCaptor.getValue().getFirstName());
        assertEquals(updatedLastName, authorCaptor.getValue().getLastName());
    }
    
    @Test
    @DisplayName("updateAuthor - Should throw EntityNotFoundException when author not found")
    void updateAuthor_whenNotFound_shouldThrowEntityNotFoundException() {
        AuthorUpdateRequest updateRequest = new AuthorUpdateRequest("Updated", "Name", LocalDate.now());
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.updateAuthor(authorId, updateRequest));
        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("addBookToAuthor - Should add book and return author's books")
    void addBookToAuthor_shouldAddBookAndReturnBooks() {
        sampleBook.setAuthors(new HashSet<>()); 
        
        List<BookDto> expectedBookDtos = List.of(sampleBookDto);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(sampleBook);
        when(bookService.findBookByIdAndAuthorId(bookId, authorId)).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(sampleAuthor);
        when(bookService.getAllBooksByAuthorId(authorId)).thenReturn(expectedBookDtos);

        List<BookDto> result = authorService.addBookToAuthor(authorId, bookId);

        assertNotNull(result);
        assertEquals(expectedBookDtos.size(), result.size());
        if (!result.isEmpty()) {
            assertEquals(expectedBookDtos.getFirst().id(), result.getFirst().id());
        }
        assertTrue(sampleBook.getAuthors().contains(sampleAuthor));
        verify(authorRepository).save(sampleAuthor);
    }

    @Test
    @DisplayName("addBookToAuthor - Should throw DataConflictException if book already in author")
    void addBookToAuthor_whenBookAlreadyInAuthor_shouldThrowDataConflictException() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(sampleBook);
        when(bookService.findBookByIdAndAuthorId(bookId, authorId)).thenReturn(Optional.of(sampleBook));

        assertThrows(DataConflictException.class, () -> authorService.addBookToAuthor(authorId, bookId));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("deleteById - Should delete author when found")
    void deleteById_whenFound_shouldDeleteAuthor() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));
        doNothing().when(authorRepository).delete(sampleAuthor);

        assertDoesNotThrow(() -> authorService.deleteById(authorId));

        verify(authorRepository).findById(authorId);
        verify(authorRepository).delete(sampleAuthor);
    }
    
    @Test
    @DisplayName("deleteById - Should throw EntityNotFoundException when not found")
    void deleteById_whenNotFound_shouldThrowEntityNotFoundException() {
        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.deleteById(authorId));

        verify(authorRepository).findById(authorId);
        verify(authorRepository, never()).delete(any(Author.class));
    }

    @Test
    @DisplayName("deleteBookFromAuthor - Should remove book from author")
    void deleteBookFromAuthor_shouldRemoveBook() {
        sampleBook.setAuthors(new HashSet<>(Set.of(sampleAuthor))); 

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(sampleBook);
        when(bookService.findBookByIdAndAuthorId(bookId, authorId)).thenReturn(Optional.of(sampleBook));
        when(authorRepository.save(any(Author.class))).thenReturn(sampleAuthor);

        assertDoesNotThrow(() -> authorService.deleteBookFromAuthor(authorId, bookId));

        assertFalse(sampleBook.getAuthors().contains(sampleAuthor));
        verify(authorRepository).save(sampleAuthor);
    }

    @Test
    @DisplayName("deleteBookFromAuthor - Should throw EntityNotFoundException if book not in author")
    void deleteBookFromAuthor_whenBookNotInAuthor_shouldThrowEntityNotFoundException() {
        sampleBook.setAuthors(new HashSet<>()); 

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(sampleAuthor));
        when(bookService.getBookByIdOrElseThrow(bookId)).thenReturn(sampleBook);
        when(bookService.findBookByIdAndAuthorId(bookId, authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authorService.deleteBookFromAuthor(authorId, bookId));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("deleteAuthorByAuthorId - Should delete author and associations")
    void deleteAuthorByAuthorId_shouldDeleteAuthorAndAssociations() {
        when(authorRepository.existsById(authorId)).thenReturn(true);
        doNothing().when(authorRepository).deleteBookAuthorByAuthorId(authorId);
        doNothing().when(authorRepository).deleteById(authorId);

        assertDoesNotThrow(() -> authorService.deleteAuthorByAuthorId(authorId));

        verify(authorRepository).existsById(authorId);
        verify(authorRepository).deleteBookAuthorByAuthorId(authorId);
        verify(authorRepository).deleteById(authorId);
    }

    @Test
    @DisplayName("deleteAuthorByAuthorId - Should throw EntityNotFoundException if author not exists")
    void deleteAuthorByAuthorId_whenAuthorNotExists_shouldThrowEntityNotFoundException() {
        when(authorRepository.existsById(authorId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> authorService.deleteAuthorByAuthorId(authorId));

        verify(authorRepository).existsById(authorId);
        verify(authorRepository, never()).deleteBookAuthorByAuthorId(any(UUID.class));
        verify(authorRepository, never()).deleteById(any(UUID.class));
    }
}
