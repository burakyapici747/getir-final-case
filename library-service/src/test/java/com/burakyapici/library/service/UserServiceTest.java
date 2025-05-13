package com.burakyapici.library.service;

import com.burakyapici.library.api.advice.DataConflictException;
import com.burakyapici.library.api.advice.EntityNotFoundException;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.common.util.UserServiceTestUtil;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.UserRepository;
import com.burakyapici.library.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Given email, when getUserByEmailOrElseThrow, then return user")
    public void givenEmail_whenGetUserByEmailOrElseThrow_thenReturnUser() {
        String email = "test@example.com";
        User expectedUser = UserServiceTestUtil.createSampleUser();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserByEmailOrElseThrow(email);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Given non-existing email, when getUserByEmailOrElseThrow, then throw EntityNotFoundException")
    public void givenNonExistingEmail_whenGetUserByEmailOrElseThrow_thenThrowEntityNotFoundException() {
        String email = "nonexisting@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmailOrElseThrow(email));
        verify(userRepository).findByEmail(email);
    }
    
    @Test
    @DisplayName("Given user id, when getUserByIdOrElseThrow, then return user")
    public void givenUserId_whenGetUserByIdOrElseThrow_thenReturnUser() {
        UUID userId = UUID.randomUUID();
        User expectedUser = UserServiceTestUtil.createSampleUserWithId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserByIdOrElseThrow(userId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Given non-existing user id, when getUserByIdOrElseThrow, then throw EntityNotFoundException")
    public void givenNonExistingUserId_whenGetUserByIdOrElseThrow_thenThrowEntityNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByIdOrElseThrow(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Given valid register request, when createUser, then return created user")
    public void givenValidRegisterRequest_whenCreateUser_thenReturnCreatedUser() {
        RegisterRequest registerRequest = UserServiceTestUtil.createSampleRegisterRequest();
        User expectedUser = UserServiceTestUtil.createSampleUser();

        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.phoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User result = userService.createUser(registerRequest);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository).existsByPhoneNumber(registerRequest.phoneNumber());
        verify(passwordEncoder).encode(registerRequest.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Given register request with mismatched passwords, when createUser, then throw BadCredentialsException")
    public void givenRegisterRequestWithMismatchedPasswords_whenCreateUser_thenThrowBadCredentialsException() {
        RegisterRequest registerRequest = new RegisterRequest(
            "email@example.com",
            "password1",
            "password2",
            "First",
            "Last",
            "1234567890",
            "Address"
        );

        assertThrows(BadCredentialsException.class, () -> userService.createUser(registerRequest));
    }

    @Test
    @DisplayName("Given register request with existing email, when createUser, then throw DataConflictException")
    public void givenRegisterRequestWithExistingEmail_whenCreateUser_thenThrowDataConflictException() {
        RegisterRequest registerRequest = UserServiceTestUtil.createSampleRegisterRequest();

        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.createUser(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.email());
    }

    @Test
    @DisplayName("Given register request with existing phone number, when createUser, then throw DataConflictException")
    public void givenRegisterRequestWithExistingPhoneNumber_whenCreateUser_thenThrowDataConflictException() {
        RegisterRequest registerRequest = UserServiceTestUtil.createSampleRegisterRequest();

        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(registerRequest.phoneNumber())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.createUser(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository).existsByPhoneNumber(registerRequest.phoneNumber());
    }

    @Test
    @DisplayName("Given page params, when getAllUsers, then return pageable user dto list")
    public void givenPageParams_whenGetAllUsers_thenReturnPageableUserDtoList() {
        int currentPage = 0;
        int pageSize = 10;
        List<User> users = UserServiceTestUtil.createSampleUsers(5);
        Page<User> userPage = UserServiceTestUtil.createUserPage(users, currentPage, pageSize, users.size());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        PageableDto<UserDto> result = userService.getAllUsers(currentPage, pageSize);

        assertNotNull(result);
        assertEquals(users.size(), result.elements().size());
        assertEquals(currentPage, result.currentPage());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Given user id, when getUserDetailById, then return user detail dto")
    public void givenUserId_whenGetUserDetailById_thenReturnUserDetailDto() {
        UUID userId = UUID.randomUUID();
        User user = UserServiceTestUtil.createSampleUserWithId(userId);
        UserDetailDto expectedUserDetailDto = UserServiceTestUtil.createSampleUserDetailDto(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDetailDto result = userService.getUserDetailById(userId);

        assertNotNull(result);
        assertEquals(expectedUserDetailDto.id(), result.id());
        assertEquals(expectedUserDetailDto.email(), result.email());
        assertEquals(expectedUserDetailDto.firstName(), result.firstName());
        assertEquals(expectedUserDetailDto.lastName(), result.lastName());
        assertEquals(expectedUserDetailDto.phoneNumber(), result.phoneNumber());
        assertEquals(expectedUserDetailDto.address(), result.address());
        assertEquals(expectedUserDetailDto.patronStatus(), result.patronStatus());

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Given user id and update request, when updateUser, then return updated user detail dto")
    public void givenUserIdAndUpdateRequest_whenUpdateUser_thenReturnUpdatedUserDetailDto() {
        UUID userId = UUID.randomUUID();
        User user = UserServiceTestUtil.createSampleUserWithId(userId);
        UserUpdateRequest updateRequest = UserServiceTestUtil.createSampleUserUpdateRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDetailDto result = userService.updateUser(userId, updateRequest);

        assertNotNull(result);
        assertEquals(userId.toString(), result.id());
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Given user id, when deleteUserById, then delete user")
    public void givenUserId_whenDeleteUserById_thenDeleteUser() {
        UUID userId = UUID.randomUUID();
        User user = UserServiceTestUtil.createSampleUserWithId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }
}
