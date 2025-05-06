package com.burakyapici.library.api.controller;

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
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
        @RequestParam(name = "page", required = false) int currentPage,
        @RequestParam(name = "size",required = false) int pageSize
    ){
        return ResponseEntity.ok(UserMapper.INSTANCE.toPageableResponse(userService.getAllUsers(currentPage, pageSize)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(UserMapper.INSTANCE.toUserDetailResponse(userService.getUserDetailById(id)));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_PATRON')")
    public ResponseEntity<UserDetailResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(
            UserMapper.INSTANCE.toUserDetailResponse(userService.getUserDetailById(userDetails.getId()))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest userUpdateRequest){
        return ResponseEntity.ok(
            UserMapper.INSTANCE.toUserDetailResponse(userService.updateUser(id, userUpdateRequest))
        );
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public ResponseEntity<?> deleteUser(@RequestParam UUID id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
