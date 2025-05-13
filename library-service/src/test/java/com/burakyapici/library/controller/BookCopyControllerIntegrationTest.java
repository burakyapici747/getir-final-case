package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.BookCopyController;
import com.burakyapici.library.api.dto.request.BookCopyCreateRequest;
import com.burakyapici.library.api.dto.request.BookCopySearchCriteria;
import com.burakyapici.library.api.dto.request.BookCopyUpdateRequest;
import com.burakyapici.library.domain.dto.BookCopyDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.enums.BookCopyStatus;
import com.burakyapici.library.service.BookCopyService;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookCopyControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private BookCopyService bookCopyService;

    @InjectMocks
    private BookCopyController bookCopyController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(bookCopyController).build();
    }

    @Test
    @DisplayName("Given valid page parameters when getAllBookCopies then returns Ok with book copy list")
    void givenValidPageParameters_whenGetAllBookCopies_thenReturnsOkWithBookCopyList() throws Exception {
        UUID bookCopyId = UUID.randomUUID();
        List<BookCopyDto> bookCopies = Collections.singletonList(
                new BookCopyDto(bookCopyId, "BC-001", BookCopyStatus.AVAILABLE)
        );
        PageableDto<BookCopyDto> pageableDto = new PageableDto<>(bookCopies, 1, 1, 0, false, false);

        when(bookCopyService.getAllBookCopies(anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/book-copies")
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
    @DisplayName("Given valid bookCopyId when getBookCopyById then returns Ok with book copy")
    void givenValidBookCopyId_whenGetBookCopyById_thenReturnsOkWithBookCopy() throws Exception {
        UUID bookCopyId = UUID.randomUUID();
        BookCopyDto bookCopyDto = new BookCopyDto(bookCopyId, "BC-002", BookCopyStatus.CHECKED_OUT);

        when(bookCopyService.getBookCopyById(bookCopyId)).thenReturn(bookCopyDto);

        mockMvc.perform(get("/api/v1/book-copies/{id}", bookCopyId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(bookCopyId.toString()))
            .andExpect(jsonPath("$.data.barcode").value("BC-002"));
    }

    @Test
    @DisplayName("Given search criteria when searchBookCopies then returns Ok with matching book copies")
    void givenSearchCriteria_whenSearchBookCopies_thenReturnsOkWithMatchingBookCopies() throws Exception {
        UUID bookCopyId = UUID.randomUUID();
        BookCopySearchCriteria criteria = new BookCopySearchCriteria("BC-003", BookCopyStatus.AVAILABLE);
        List<BookCopyDto> bookCopies = Collections.singletonList(
                new BookCopyDto(bookCopyId, "BC-003", BookCopyStatus.AVAILABLE)
        );
        PageableDto<BookCopyDto> pageableDto = new PageableDto<>(bookCopies, 1, 1, 0, false, false);

        when(bookCopyService.searchBookCopies(eq(criteria), anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/book-copies/search")
            .param("barcode", "BC-003")
            .param("status", "AVAILABLE")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.elements[0].id").value(bookCopyId.toString()))
            .andExpect(jsonPath("$.data.elements[0].barcode").value("BC-003"));
    }

    @Test
    @DisplayName("Given valid book copy data when createBookCopy then returns Created with new book copy")
    void givenValidBookCopyData_whenCreateBookCopy_thenReturnsCreatedWithNewBookCopy() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID bookCopyId = UUID.randomUUID();
        BookCopyCreateRequest request = new BookCopyCreateRequest("BC-NEW-001", bookId, BookCopyStatus.AVAILABLE);
        BookCopyDto createdBookCopy = new BookCopyDto(bookCopyId, "BC-NEW-001", BookCopyStatus.AVAILABLE);

        when(bookCopyService.createBookCopy(any(BookCopyCreateRequest.class))).thenReturn(createdBookCopy);

        mockMvc.perform(post("/api/v1/book-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(bookCopyId.toString()))
            .andExpect(jsonPath("$.data.barcode").value("BC-NEW-001"));
    }

    @Test
    @DisplayName("Given bookCopyId and update data when updateBookCopy then returns Ok with updated book copy")
    void givenBookCopyIdAndUpdateData_whenUpdateBookCopy_thenReturnsOkWithUpdatedBookCopy() throws Exception {
        UUID bookCopyId = UUID.randomUUID();
        BookCopyUpdateRequest request = new BookCopyUpdateRequest(BookCopyStatus.IN_REPAIR);
        BookCopyDto updatedBookCopy = new BookCopyDto(bookCopyId, "BC-UPDATED-001", BookCopyStatus.IN_REPAIR);

        when(bookCopyService.updateBookCopyById(eq(bookCopyId), any(BookCopyUpdateRequest.class))).thenReturn(updatedBookCopy);

        mockMvc.perform(put("/api/v1/book-copies/{id}", bookCopyId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(bookCopyId.toString()))
            .andExpect(jsonPath("$.data.status").value(BookCopyStatus.IN_REPAIR.toString()));
    }

    @Test
    @DisplayName("Given bookCopyId when deleteBookCopyById then returns Ok with success message")
    void givenBookCopyId_whenDeleteBookCopyById_thenReturnsOkWithSuccessMessage() throws Exception {
        UUID bookCopyId = UUID.randomUUID();
        doNothing().when(bookCopyService).deleteBookCopyById(bookCopyId);

        mockMvc.perform(delete("/api/v1/book-copies/{id}", bookCopyId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Book copy deleted successfully"));
    }
}
