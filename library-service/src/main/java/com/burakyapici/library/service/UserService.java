package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.model.User;

import java.util.UUID;

public interface UserService {
    User getUserByEmailOrElseThrow(String email);
    User getUserByIdOrElseThrow(UUID id);
    User createUser(RegisterRequest registerRequest);
    PageableDto<UserDto> getAllUsers(int page, int size);
    UserDetailDto getUserDetailById(UUID id);
    UserDetailDto updateUser(UUID id, UserUpdateRequest userUpdateRequest);
    void deleteUserById(UUID id);
}
