package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.BookController;
import com.burakyapici.library.api.dto.request.BookAvailabilityUpdateEvent;
import com.burakyapici.library.api.dto.request.BookCreateRequest;
import com.burakyapici.library.api.dto.request.BookUpdateRequest;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.BookDetailDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @Spy
    private Sinks.Many<BookAvailabilityUpdateEvent> bookAvailabilitySink = Sinks.many().replay().limit(1);

    @Spy
    private Flux<BookAvailabilityUpdateEvent> bookAvailabilityFlux = bookAvailabilitySink.asFlux();

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    @DisplayName("Given valid page parameters when getAllBooks then returns Ok with book list")
    void givenValidPageParameters_whenGetAllBooks_thenReturnsOkWithBookList() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<BookDto> books = Collections.singletonList(
                new BookDto(bookId.toString(), "Effective Java", "978-0134685991", BookStatus.ACTIVE, LocalDate.now(), 416, Collections.emptyList(), Collections.emptyList())
        );
        PageableDto<BookDto> pageableDto = new PageableDto<>(books, 1, 1, 0, false, false);

        when(bookService.getAllBooks(anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/books")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.elements[0].id").value(bookId.toString()))
            .andExpect(jsonPath("$.data.elements[0].title").value("Effective Java"));
    }

    @Test
    @DisplayName("Given valid bookId when getBookDetail then returns Ok with book detail")
    void givenValidBookId_whenGetBookDetail_thenReturnsOkWithBookDetail() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookDetailDto bookDetailDto = new BookDetailDto(
                bookId.toString(),
                "Effective Java",
                "978-0134685991",
                BookStatus.ACTIVE,
                416,
                LocalDate.now(),
                10,
                Collections.singletonList(new AuthorDto(UUID.randomUUID(), "Joshua", "Bloch", LocalDate.now())),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(bookService.getBookDetailById(bookId)).thenReturn(bookDetailDto);

        mockMvc.perform(get("/api/v1/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(bookId.toString()))
                .andExpect(jsonPath("$.data.title").value("Effective Java"));
    }

    @Test
    @DisplayName("Given search criteria when searchBooks then returns Ok with matching books")
    void givenSearchCriteria_whenSearchBooks_thenReturnsOkWithMatchingBooks() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<BookDto> books = Collections.singletonList(
            new BookDto(bookId.toString(), "Effective Java", "978-0-13-468599-1", BookStatus.ACTIVE, LocalDate.now(), 416, Collections.emptyList(), Collections.emptyList())
        );
        PageableDto<BookDto> pageableDto = new PageableDto<>(books, 1, 1, 0, false, false);

        when(bookService.searchBooks(any(), anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/books/search")
                        .param("title", "Effective Java")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(bookId.toString()))
                .andExpect(jsonPath("$.data.elements[0].title").value("Effective Java"));
    }

    @Test
    @DisplayName("Given valid bookId and page parameters when getBookCopies then returns Ok with book copy list")
    void givenValidBookIdAndPageParameters_whenGetBookCopies_thenReturnsOkWithBookCopyList() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID bookCopyId = UUID.randomUUID();
        List<BookCopyDto> bookCopies = Collections.singletonList(
                new BookCopyDto(bookCopyId, "BC-001", com.burakyapici.library.domain.enums.BookCopyStatus.AVAILABLE)
        );
        PageableDto<BookCopyDto> pageableDto = new PageableDto<>(bookCopies, 1, 1, 0, false, false);

        when(bookService.getBookCopiesById(eq(bookId), anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/books/{id}/copies", bookId)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(bookCopyId.toString()))
                .andExpect(jsonPath("$.data.elements[0].barcode").value("BC-001"));
    }

    @Test
    @DisplayName("Given valid book data when createBook then returns Created with new book")
    void givenValidBookData_whenCreateBook_thenReturnsCreatedWithNewBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        BookCreateRequest request = new BookCreateRequest(
                "Effective Java",
                "978-0-13-468599-1",
                BookStatus.ACTIVE,
                416,
                LocalDate.now(),
                Set.of(authorId),
                Set.of(genreId)
        );
        BookDto createdBook =
                new BookDto(
                    bookId.toString(),
                    "Effective Java",
                    "978-0134685991",
                    BookStatus.ACTIVE,
                    LocalDate.now(),
                    416,
                    Collections.emptyList(),
                    Collections.emptyList()
                );

        when(bookService.createBook(any(BookCreateRequest.class))).thenReturn(createdBook);

        mockMvc.perform(post("/api/v1/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(bookId.toString()))
            .andExpect(jsonPath("$.data.title").value("Effective Java"));
    }

    @Test
    @DisplayName("Given bookId and update data when updateBook then returns Ok with updated book")
    void givenBookIdAndUpdateData_whenUpdateBook_thenReturnsOkWithUpdatedBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookUpdateRequest request = new BookUpdateRequest("Effective Java - 3rd Edition", BookStatus.ACTIVE, 420, LocalDate.now(), null, null);
        BookDto updatedBook = new BookDto(bookId.toString(), "Effective Java - 3rd Edition", "978-0134685991", BookStatus.ACTIVE, LocalDate.now(), 420, Collections.emptyList(), Collections.emptyList());

        when(bookService.updateBook(eq(bookId), any(BookUpdateRequest.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/v1/books/{id}", bookId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(bookId.toString()))
            .andExpect(jsonPath("$.data.title").value("Effective Java - 3rd Edition"));
    }

    @Test
    @DisplayName("Given bookId when deleteBook then returns Ok with success message")
    void givenBookId_whenDeleteBook_thenReturnsOkWithSuccessMessage() throws Exception {
        UUID bookId = UUID.randomUUID();
        doNothing().when(bookService).deleteById(bookId);

        mockMvc.perform(delete("/api/v1/books/{id}", bookId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book deleted successfully."));
    }

    @Test
    @DisplayName("Given bookId when streamBookAvailabilityUpdates then returns event stream")
    void givenBookId_whenStreamBookAvailabilityUpdates_thenReturnsEventStream() throws Exception {
        UUID bookId = UUID.randomUUID();
        int initialCopies = 5;
        BookAvailabilityUpdateEvent expectedEvent = new BookAvailabilityUpdateEvent(bookId, initialCopies);

        when(bookService.calculateAvailableCopiesCount(bookId)).thenReturn(initialCopies);

        mockMvc.perform(get("/api/v1/books/{id}/book-availability", bookId)
            .accept(MediaType.TEXT_EVENT_STREAM))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("data:" + objectMapper.writeValueAsString(expectedEvent))));

        verify(bookAvailabilitySink, times(1)).tryEmitNext(eq(expectedEvent));
    }
}
