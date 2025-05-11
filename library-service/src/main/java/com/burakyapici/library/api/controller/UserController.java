package com.burakyapici.library.api.controller;

import com.burakyapici.library.api.ApiResponse;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.api.dto.response.PageableResponse;
import com.burakyapici.library.api.dto.response.UserDetailResponse;
import com.burakyapici.library.common.mapper.UserMapper;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<PageableResponse<UserDto>>> getAllUsers(
        @RequestParam(name = "page", defaultValue = "0", required = false) int currentPage,
        @RequestParam(name = "size", defaultValue = "10", required = false) int pageSize
    ){
        PageableResponse<UserDto> users = UserMapper.INSTANCE.pageableDtoToPageableResponse(
            userService.getAllUsers(currentPage, pageSize)
        );
        return ApiResponse.okResponse(users, "Users retrieved successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable UUID id){
        UserDetailResponse user = UserMapper.INSTANCE.userDetailDtoToUserDetailResponse(
            userService.getUserDetailById(id)
        );
        return ApiResponse.okResponse(user, "User details retrieved successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_PATRON')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        UserDetailResponse user = UserMapper.INSTANCE.userDetailDtoToUserDetailResponse(
            userService.getUserDetailById(userDetails.getId())
        );
        return ApiResponse.okResponse(user, "Current user details retrieved successfully");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUser(
        @PathVariable UUID id,
        @RequestBody UserUpdateRequest userUpdateRequest
    ){
        UserDetailResponse updatedUser = UserMapper.INSTANCE.userDetailDtoToUserDetailResponse(
            userService.updateUser(id, userUpdateRequest)
        );
        return ApiResponse.okResponse(updatedUser, "User updated successfully");
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestParam UUID id){
        userService.deleteUserById(id);
        return ApiResponse.noContentResponse("User deleted successfully");
    }
}