package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.BorrowingController;
import com.burakyapici.library.api.dto.request.BorrowBookCopyRequest;
import com.burakyapici.library.api.dto.request.BorrowReturnRequest;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.enums.BorrowingStatus;
import com.burakyapici.library.domain.enums.ReturnType;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BorrowingControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private BorrowingService borrowingService;

    @InjectMocks
    private BorrowingController borrowingController;

    private ObjectMapper objectMapper;
    private UserDetailsImpl mockLibrarian;
    private UUID librarianId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(borrowingController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        librarianId = UUID.randomUUID();
        mockLibrarian = new UserDetailsImpl(
                librarianId,
                "librarian@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_LIBRARIAN"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(mockLibrarian, null, mockLibrarian.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Given valid barcode and request when borrowBookCopyByBarcode then returns Ok with borrowing details")
    void givenValidBarcodeAndRequest_whenBorrowBookCopyByBarcode_thenReturnsOkWithBorrowingDetails() throws Exception {
        String barcode = "BC-001";
        UUID patronId = UUID.randomUUID();
        BorrowBookCopyRequest request = new BorrowBookCopyRequest(patronId);

        UUID borrowingId = UUID.randomUUID();
        BorrowingDto borrowingDto = new BorrowingDto(
                borrowingId, patronId, "patron@example.com", "Patron", "User",
                UUID.randomUUID(), barcode, UUID.randomUUID(), "Test Book", "12345",
                librarianId, "librarian@example.com", null, null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(14), null, BorrowingStatus.BORROWED
        );

        when(borrowingService.borrowBookCopyByBarcode(eq(barcode), any(BorrowBookCopyRequest.class), eq(mockLibrarian))).thenReturn(borrowingDto);

        mockMvc.perform(post("/api/v1/borrowing/{barcode}", barcode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(borrowingId.toString()))
                .andExpect(jsonPath("$.data.bookCopyBarcode").value(barcode))
                .andExpect(jsonPath("$.data.status").value(BorrowingStatus.BORROWED.toString()));
    }

    @Test
    @DisplayName("Given valid barcode and return request when returnBookCopyByBarcode then returns Ok with updated borrowing details")
    void givenValidBarcodeAndReturnRequest_whenReturnBookCopyByBarcode_thenReturnsOkWithUpdatedBorrowingDetails() throws Exception {
        String barcode = "BC-002";
        UUID patronId = UUID.randomUUID();
        BorrowReturnRequest request = new BorrowReturnRequest(patronId, ReturnType.NORMAL);

        UUID borrowingId = UUID.randomUUID();
        UUID oldLibrarianId = UUID.randomUUID();
        BorrowingDto borrowingDto = new BorrowingDto(
                borrowingId, patronId, "patron@example.com", "Patron", "User",
                UUID.randomUUID(), barcode, UUID.randomUUID(), "Another Book", "67890",
                oldLibrarianId, "oldlibrarian@example.com", librarianId, "librarian@example.com",
                LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(9), LocalDateTime.now(), BorrowingStatus.RETURNED
        );

        when(borrowingService.returnBookCopyByBarcode(eq(barcode), any(BorrowReturnRequest.class), eq(mockLibrarian))).thenReturn(borrowingDto);

        mockMvc.perform(patch("/api/v1/borrowing/{barcode}", barcode)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(borrowingId.toString()))
                .andExpect(jsonPath("$.data.bookCopyBarcode").value(barcode))
                .andExpect(jsonPath("$.data.status").value(BorrowingStatus.RETURNED.toString()));
    }
}
