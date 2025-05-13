package com.burakyapici.library.controller;

import com.burakyapici.library.api.controller.UserController;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.enums.BorrowStatus;
import com.burakyapici.library.domain.enums.PatronStatus;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.UserService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private BorrowingService borrowingService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDetailsImpl mockLibrarian;
    private UUID librarianId;
    private UserDetailsImpl mockPatron;
    private UUID patronId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        librarianId = UUID.randomUUID();
        mockLibrarian = new UserDetailsImpl(librarianId, "librarian@example.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_LIBRARIAN")));

        patronId = UUID.randomUUID();
        mockPatron = new UserDetailsImpl(patronId, "patron@example.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATRON")));
    }

    private void setupAuthentication(UserDetailsImpl userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Given user is librarian when getAllUsers then returns Ok with user list")
    void givenUserIsLibrarian_whenGetAllUsers_thenReturnsOkWithUserList() throws Exception {
        setupAuthentication(mockLibrarian);
        PageableParams params = new PageableParams(0, 10);
        UUID userId = UUID.randomUUID();
        List<UserDto> userList = Collections.singletonList(
                new UserDto(userId.toString(), "user@example.com", "Test", "User", "1234567890", "Test Address", PatronStatus.ACTIVE)
        );
        PageableDto<UserDto> pageableDto = new PageableDto<>(userList, 1, 1, 0, false, false);

        when(userService.getAllUsers(params.page(), params.size())).thenReturn(pageableDto);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", String.valueOf(params.page()))
                        .param("size", String.valueOf(params.size()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.elements[0].id").value(userId.toString()));
    }

    @Test
    @DisplayName("Given user is librarian and valid userId when getUserById then returns Ok with user details")
    void givenUserIsLibrarianAndValidUserId_whenGetUserById_thenReturnsOkWithUserDetails() throws Exception {
        setupAuthentication(mockLibrarian);
        UUID userIdToFetch = UUID.randomUUID();
        UserDetailDto userDetailDto = new UserDetailDto(userIdToFetch.toString(), "fetch@example.com", "Fetched", "User", "0987654321", "Fetched Address", PatronStatus.ACTIVE);

        when(userService.getUserDetailById(userIdToFetch)).thenReturn(userDetailDto);

        mockMvc.perform(get("/api/v1/users/{id}", userIdToFetch)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(userIdToFetch.toString()))
                .andExpect(jsonPath("$.data.email").value("fetch@example.com"));
    }

    @Test
    @DisplayName("Given authenticated user when getCurrentUser then returns Ok with current user details")
    void givenAuthenticatedUser_whenGetCurrentUser_thenReturnsOkWithCurrentUserDetails() throws Exception {
        setupAuthentication(mockPatron);
        UserDetailDto userDetailDto = new UserDetailDto(patronId.toString(), "patron@example.com", "Patron", "User", "5551234567", "Patron Address", PatronStatus.ACTIVE);

        when(userService.getUserDetailById(patronId)).thenReturn(userDetailDto);

        mockMvc.perform(get("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(patronId.toString()))
                .andExpect(jsonPath("$.data.email").value("patron@example.com"));
    }

    @Test
    @DisplayName("Given authenticated user when getCurrentUserBorrowings then returns Ok with their borrowings")
    void givenAuthenticatedUser_whenGetCurrentUserBorrowings_thenReturnsOkWithTheirBorrowings() throws Exception {
        setupAuthentication(mockPatron);
        UUID borrowingId = UUID.randomUUID();
        List<BorrowingDto> borrowings = Collections.singletonList(
                new BorrowingDto(borrowingId, patronId, "patron@example.com", "Patron", "User", UUID.randomUUID(), "BC-001", UUID.randomUUID(), "Test Book", "ISBN123", UUID.randomUUID(), "Librarian", null, null, LocalDateTime.now(), LocalDateTime.now().plusDays(14), null, BorrowStatus.BORROWED)
        );

        when(borrowingService.getCurrentUserBorrowings(patronId)).thenReturn(borrowings);

        mockMvc.perform(get("/api/v1/users/me/borrowings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(borrowingId.toString()))
                .andExpect(jsonPath("$.data[0].userId").value(patronId.toString()));
    }

    @Test
    @DisplayName("Given user is librarian and valid userId when getUserBorrowings then returns Ok with user borrowings")
    void givenUserIsLibrarianAndValidUserId_whenGetUserBorrowings_thenReturnsOkWithUserBorrowings() throws Exception {
        setupAuthentication(mockLibrarian);
        UUID userIdToFetchBorrowings = UUID.randomUUID();
        UUID borrowingId = UUID.randomUUID();
        List<BorrowingDto> borrowings = Collections.singletonList(
                new BorrowingDto(borrowingId, userIdToFetchBorrowings, "user@borrow.com", "Borrow", "User", UUID.randomUUID(), "BC-002", UUID.randomUUID(), "Another Book", "ISBN456", UUID.randomUUID(), "Librarian", null, null, LocalDateTime.now(), LocalDateTime.now().plusDays(10), null, BorrowStatus.BORROWED)
        );

        when(borrowingService.getUserBorrowingsById(userIdToFetchBorrowings)).thenReturn(borrowings);

        mockMvc.perform(get("/api/v1/users/{id}/borrowings", userIdToFetchBorrowings)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(borrowingId.toString()))
                .andExpect(jsonPath("$.data[0].userId").value(userIdToFetchBorrowings.toString()));
    }

    @Test
    @DisplayName("Given user is librarian and valid update request when updateUser then returns Ok with updated user details")
    void givenUserIsLibrarianAndValidUpdateRequest_whenUpdateUser_thenReturnsOkWithUpdatedUserDetails() throws Exception {
        setupAuthentication(mockLibrarian);
        UUID userIdToUpdate = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("update@example.com", "Updated", "Name", "+01234567890", "Updated Address", PatronStatus.INACTIVE);
        UserDetailDto updatedUserDetail = new UserDetailDto(userIdToUpdate.toString(), "update@example.com", "Updated", "Name", "+01234567890", "Updated Address", PatronStatus.INACTIVE);

        when(userService.updateUser(eq(userIdToUpdate), any(UserUpdateRequest.class))).thenReturn(updatedUserDetail);

        mockMvc.perform(put("/api/v1/users/{id}", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(userIdToUpdate.toString()))
                .andExpect(jsonPath("$.data.firstName").value("Updated"));
    }

    @Test
    @DisplayName("Given user is librarian and valid userId when deleteUser then returns Ok with success message")
    void givenUserIsLibrarianAndValidUserId_whenDeleteUser_thenReturnsOkWithSuccessMessage() throws Exception {
        setupAuthentication(mockLibrarian);
        UUID userIdToDelete = UUID.randomUUID();

        doNothing().when(userService).deleteUserById(userIdToDelete);

        mockMvc.perform(delete("/api/v1/users")
                        .param("id", userIdToDelete.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
