package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.AuthorController;
import com.burakyapici.library.api.dto.request.AuthorCreateRequest;
import com.burakyapici.library.api.dto.request.AuthorSearchCriteria;
import com.burakyapici.library.api.dto.request.AuthorUpdateRequest;
import com.burakyapici.library.domain.dto.AuthorDto;
import com.burakyapici.library.domain.dto.BookDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookStatus;
import com.burakyapici.library.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthorControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
    }

    @Test
    @DisplayName("Given valid page parameters when getAllAuthors then returns Ok with author list")
    void givenValidPageParameters_whenGetAllAuthors_thenReturnsOkWithAuthorList() throws Exception {
        UUID authorId = UUID.randomUUID();
        List<AuthorDto> authors = Collections.singletonList(
                new AuthorDto(authorId, "John", "Doe", LocalDate.of(1980, 1, 1))
        );
        PageableDto<AuthorDto> pageableDto = new PageableDto<>(authors, 1, 1, 0, false, false);

        when(authorService.getAllAuthors(anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/authors")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(authorId.toString()))
                .andExpect(jsonPath("$.data.elements[0].firstName").value("John"));
    }

    @Test
    @DisplayName("Given valid authorId when getAuthorById then returns Ok with author")
    void givenValidAuthorId_whenGetAuthorById_thenReturnsOkWithAuthor() throws Exception {
        UUID authorId = UUID.randomUUID();
        AuthorDto authorDto = new AuthorDto(authorId, "John", "Doe", LocalDate.of(1980, 1, 1));

        when(authorService.getAuthorById(authorId)).thenReturn(authorDto);

        mockMvc.perform(get("/api/v1/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorId.toString()))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("Given search criteria when searchAuthors then returns Ok with matching authors")
    void givenSearchCriteria_whenSearchAuthors_thenReturnsOkWithMatchingAuthors() throws Exception {
        UUID authorId = UUID.randomUUID();
        AuthorSearchCriteria criteria = new AuthorSearchCriteria("John", null, null);
        List<AuthorDto> authors = Collections.singletonList(
                new AuthorDto(authorId, "John", "Doe", LocalDate.of(1980, 1, 1))
        );
        PageableDto<AuthorDto> pageableDto = new PageableDto<>(authors, 1, 1, 0, false, false);

        when(authorService.searchAuthors(eq(criteria), anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/authors/search")
                        .param("firstName", "John")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(authorId.toString()))
                .andExpect(jsonPath("$.data.elements[0].firstName").value("John"));
    }

    @Test
    @DisplayName("Given valid author data when createAuthor then returns Created with new author")
    void givenValidAuthorData_whenCreateAuthor_thenReturnsCreatedWithNewAuthor() throws Exception {
        UUID authorId = UUID.randomUUID();
        AuthorCreateRequest request = new AuthorCreateRequest("John", "Doe", LocalDate.of(1980, 1, 1));
        AuthorDto createdAuthor = new AuthorDto(authorId, "John", "Doe", LocalDate.of(1980, 1, 1));

        when(authorService.createAuthor(any(AuthorCreateRequest.class))).thenReturn(createdAuthor);

        mockMvc.perform(post("/api/v1/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorId.toString()))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("Given authorId and bookId when addBookToAuthor then returns Ok with updated book list")
    void givenAuthorIdAndBookId_whenAddBookToAuthor_thenReturnsOkWithUpdatedBookList() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        List<BookDto> bookDtos = List.of(
                new BookDto(bookId.toString(), "Test Book", "1234567890123", BookStatus.ACTIVE, LocalDate.now(), 200, Collections.emptyList(), Collections.emptyList())
        );

        when(authorService.addBookToAuthor(authorId, bookId)).thenReturn(bookDtos);

        mockMvc.perform(post("/api/v1/authors/{id}/books/{bookId}", authorId, bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(bookId.toString()))
                .andExpect(jsonPath("$.data[0].title").value("Test Book"));
    }

    @Test
    @DisplayName("Given authorId and update data when updateAuthor then returns Ok with updated author")
    void givenAuthorIdAndUpdateData_whenUpdateAuthor_thenReturnsOkWithUpdatedAuthor() throws Exception {
        UUID authorId = UUID.randomUUID();
        AuthorUpdateRequest request = new AuthorUpdateRequest("Jane", "Smith", LocalDate.of(1985, 5, 15));
        AuthorDto updatedAuthor = new AuthorDto(authorId, "Jane", "Smith", LocalDate.of(1985, 5, 15));

        when(authorService.updateAuthor(eq(authorId), any(AuthorUpdateRequest.class))).thenReturn(updatedAuthor);

        mockMvc.perform(put("/api/v1/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(authorId.toString()))
                .andExpect(jsonPath("$.data.firstName").value("Jane"));
    }

    @Test
    @DisplayName("Given authorId when deleteById then returns NoContent")
    void givenAuthorId_whenDeleteById_thenReturnsNoContent() throws Exception {
        UUID authorId = UUID.randomUUID();
        doNothing().when(authorService).deleteAuthorByAuthorId(authorId);

        mockMvc.perform(delete("/api/v1/authors/{id}", authorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Author deleted successfully"));
    }

    @Test
    @DisplayName("Given authorId and bookId when removeBookFromAuthor then returns NoContent")
    void givenAuthorIdAndBookId_whenRemoveBookFromAuthor_thenReturnsNoContent() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        doNothing().when(authorService).deleteBookFromAuthor(authorId, bookId);

        mockMvc.perform(delete("/api/v1/authors/{id}/books/{bookId}", authorId, bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book removed from author successfully"));
    }
}
