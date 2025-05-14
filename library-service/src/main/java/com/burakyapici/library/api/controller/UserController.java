package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.dto.response.ApiResponse;
import com.burakyapici.library.api.dto.request.PageableParams;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.UserDetailResponse;
import com.burakyapici.library.common.mapper.UserMapper;
import com.burakyapici.library.domain.dto.BorrowingDto;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.BorrowingService;
import com.burakyapici.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Operations related to user management")
public class UserController {
    private final UserService userService;
    private final BorrowingService borrowingService;

    public UserController(UserService userService, BorrowingService borrowingService) {
        this.userService = userService;
        this.borrowingService = borrowingService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PageableResponse<UserDto>>> getAllUsers(@Valid PageableParams params) {
        PageableDto<UserDto> pageResult = userService.getAllUsers(params.page(), params.size());
        PageableResponse<UserDto> response = UserMapper.INSTANCE.toPageableResponse(pageResult);
        return ApiResponse.okResponse(response, "Users retrieved successfully");
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Get user by ID", description = "Retrieves detailed information of a specific user. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(
        @Parameter(description = "UUID of the user") @PathVariable("id") @NotNull UUID id
    ) {
        UserDetailResponse user = UserMapper.INSTANCE.toUserDetailResponse(
            userService.getUserDetailById(id)
        );
        return ApiResponse.okResponse(user, "User details retrieved successfully");
    }

    @GetMapping(path = "/me")
    @Operation(summary = "Get current user details", description = "Returns the information of the authenticated user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Current user details retrieved successfully")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        UserDetailResponse user = UserMapper.INSTANCE.toUserDetailResponse(
            userService.getUserDetailById(userDetails.getId())
        );
        return ApiResponse.okResponse(user, "Current user details retrieved successfully");
    }

    @GetMapping(path = "/me/borrowings")
    @Operation(summary = "Get borrowings of current user", description = "Returns all borrowings of the authenticated user.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Current user borrowings retrieved successfully")
    public ResponseEntity<ApiResponse<List<BorrowingDto>>> getCurrentUserBorrowings(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        List<BorrowingDto> borrowings = borrowingService.getCurrentUserBorrowings(userDetails.getId());
        return ApiResponse.okResponse(borrowings, "Current user borrowings retrieved successfully");
    }

    @GetMapping(path = "/{id}/borrowings")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Get borrowings of a user", description = "Returns borrowings of a specific user. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User borrowings retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<BorrowingDto>>> getUserBorrowings(
        @Parameter(description = "UUID of the user") @PathVariable("id") @NotNull UUID id
    ){
        List<BorrowingDto> borrowings = borrowingService.getUserBorrowingsById(id);
        return ApiResponse.okResponse(borrowings, "User borrowings retrieved successfully");
    }

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user", description = "Updates a user's details. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUser(
        @Parameter(description = "UUID of the user") @PathVariable("id") @NotNull UUID id,
        @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ){
        UserDetailResponse updatedUser = UserMapper.INSTANCE.toUserDetailResponse(
            userService.updateUser(id, userUpdateRequest)
        );
        return ApiResponse.okResponse(updatedUser, "User updated successfully");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Delete user", description = "Deletes a user by ID. Accessible only to librarians.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @Parameter(description = "UUID of the user to delete") @RequestParam("id") @NotNull UUID id
    ){
        userService.deleteUserById(id);
        return ApiResponse.noContentResponse("User deleted successfully");
    }
}
