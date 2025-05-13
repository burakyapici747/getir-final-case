package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.WaitListController;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.request.PlaceHoldRequest;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.WaitListDto;
import com.burakyapici.library.domain.enums.WaitListStatus;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.WaitListService;
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
import java.time.format.DateTimeFormatter; // Keep for potential direct formatting
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
public class WaitListControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private WaitListService waitListService;

    @InjectMocks
    private WaitListController waitListController;

    private ObjectMapper objectMapper;
    private UserDetailsImpl mockPatron;
    private UUID patronId;
    private UserDetailsImpl mockLibrarian;
    private UUID librarianId;
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // Formatter for date strings

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(waitListController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        patronId = UUID.randomUUID();
        mockPatron = new UserDetailsImpl(patronId, "patron@example.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATRON")));

        librarianId = UUID.randomUUID();
        mockLibrarian = new UserDetailsImpl(librarianId, "librarian@example.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_LIBRARIAN")));
    }

    private void setupAuthentication(UserDetailsImpl userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Given user is patron when getMyHolds then returns Ok with their holds list")
    void givenUserIsPatron_whenGetMyHolds_thenReturnsOkWithTheirHoldsList() throws Exception {
        setupAuthentication(mockPatron);
        UUID waitListId = UUID.randomUUID();
        String nowString = LocalDateTime.now().format(formatter);
        List<WaitListDto> waitListDtos = List.of(
                new WaitListDto(waitListId, nowString, null, WaitListStatus.WAITING)
        );

        when(waitListService.getWaitListsByPatronId(patronId)).thenReturn(waitListDtos);

        mockMvc.perform(get("/api/v1/waitlist/my-holds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(waitListId.toString()))
                .andExpect(jsonPath("$.data[0].status").value(WaitListStatus.WAITING.toString()));
    }

    @Test
    @DisplayName("Given valid bookId and page params when getWaitListForBook then returns Ok with book wait list")
    void givenValidBookIdAndPageParams_whenGetWaitListForBook_thenReturnsOkWithBookWaitList() throws Exception {
        UUID bookId = UUID.randomUUID();
        UUID waitListId = UUID.randomUUID();
        PageableParams params = new PageableParams(0,10);
        String nowString = LocalDateTime.now().format(formatter);
        List<WaitListDto> waitListContent = List.of(
            new WaitListDto(waitListId, nowString, null, WaitListStatus.WAITING)
        );
        PageableDto<WaitListDto> pageableDto = new PageableDto<>(waitListContent, 1, 1, 0, false, false);

        when(waitListService.getWaitListsByBookId(eq(bookId), anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/waitlist/book/{bookId}", bookId)
                        .param("page", String.valueOf(params.page()))
                        .param("size", String.valueOf(params.size()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(waitListId.toString()));
    }

    @Test
    @DisplayName("Given user is librarian when getAllWaitLists then returns Ok with all wait lists")
    void givenUserIsLibrarian_whenGetAllWaitLists_thenReturnsOkWithAllWaitLists() throws Exception {
        setupAuthentication(mockLibrarian);
        UUID waitListId = UUID.randomUUID();
        PageableParams params = new PageableParams(0,10);
        String nowString = LocalDateTime.now().format(formatter);
        List<WaitListDto> waitListContent = List.of(
             new WaitListDto(waitListId, nowString, null, WaitListStatus.WAITING)
        );
        PageableDto<WaitListDto> pageableDto = new PageableDto<>(waitListContent, 1, 1, 0, false, false);

        when(waitListService.getAllWaitLists(anyInt(), anyInt())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/waitlist")
            .param("page", String.valueOf(params.page()))
            .param("size", String.valueOf(params.size()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.elements[0].id").value(waitListId.toString()));
    }

    @Test
    @DisplayName("Given valid place hold request and authenticated user when placeHold then returns Created with wait list details")
    void givenValidPlaceHoldRequestAndAuthenticatedUser_whenPlaceHold_thenReturnsCreatedWithWaitListDetails() throws Exception {
        setupAuthentication(mockPatron);
        UUID bookId = UUID.randomUUID();
        PlaceHoldRequest request = new PlaceHoldRequest(bookId);
        UUID waitListId = UUID.randomUUID();
        String nowString = LocalDateTime.now().format(formatter);
        WaitListDto waitListDto = new WaitListDto(waitListId, nowString, null, WaitListStatus.WAITING);

        when(waitListService.placeHold(any(PlaceHoldRequest.class), eq(patronId))).thenReturn(waitListDto);

        mockMvc.perform(post("/api/v1/waitlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(waitListId.toString()))
                .andExpect(jsonPath("$.data.status").value(WaitListStatus.WAITING.toString()));
    }

    @Test
    @DisplayName("Given valid waitListId and authenticated user when cancelHold then returns Ok with success message")
    void givenValidWaitListIdAndAuthenticatedUser_whenCancelHold_thenReturnsOkWithSuccessMessage() throws Exception {
        setupAuthentication(mockPatron);
        UUID waitListId = UUID.randomUUID();

        doNothing().when(waitListService).cancelHold(eq(waitListId), eq(patronId));

        mockMvc.perform(delete("/api/v1/waitlist/{waitListId}", waitListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Hold cancelled successfully"));
    }
}
