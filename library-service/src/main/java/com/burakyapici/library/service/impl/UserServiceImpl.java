package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.request.UserUpdateRequest;
import com.burakyapici.library.common.mapper.UserMapper;
import com.burakyapici.library.domain.dto.PageableDto;
import com.burakyapici.library.domain.dto.UserDetailDto;
import com.burakyapici.library.domain.dto.UserDto;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.UserRepository;
import com.burakyapici.library.exception.UserAlreadyExistException;
import com.burakyapici.library.exception.UserNotFoundException;
import com.burakyapici.library.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final int TOTAL_ELEMENTS_PER_PAGE = 10;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmailOrElseThrow(String email) {
        return findByEmail(email);
    }

    @Override
    public User getUserByIdOrElseThrow(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id!"));
    }

    @Override
    public User createUser(RegisterRequest registerRequest) {
        ensurePasswordsMatch(registerRequest);
        ensureEmailNotTaken(registerRequest.email());
        ensurePhoneNumberNotTaken(registerRequest.phoneNumber());

        User newUser = User.builder()
            .email(registerRequest.email())
            .passwordHash(passwordEncoder.encode(registerRequest.password()))
            .firstName(registerRequest.firstName())
            .lastName(registerRequest.lastName())
            .phoneNumber(registerRequest.phoneNumber())
            .address(registerRequest.address())
            .role(Role.PATRON)
            .build();

        return userRepository.save(newUser);
    }

    @Override
    public PageableDto<UserDto> getAllUsers(int currentPage, int pageSize) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(currentPage);
        Page<User> allUsersPage = userRepository.findAll(pageable);
        List<UserDto> userDtoList = UserMapper.INSTANCE.userListToUserDtoList(allUsersPage.getContent());

        return new PageableDto<>(
            userDtoList,
            allUsersPage.getTotalPages(),
            TOTAL_ELEMENTS_PER_PAGE,
            currentPage,
            allUsersPage.hasNext(),
            allUsersPage.hasPrevious()
        );
    }

    @Override
    public UserDetailDto getUserDetailById(UUID id) {
        User user = findById(id);
        return UserMapper.INSTANCE.userToUserDetailDto(user);
    }

    @Override
    public UserDetailDto updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        User user = findById(id);

        UserMapper.INSTANCE.updateUserFromUserUpdateRequest(userUpdateRequest, user);

        return UserMapper.INSTANCE.userToUserDetailDto(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private void ensureEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException("User already exists with email!");
        }
    }

    private void ensurePhoneNumberNotTaken(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new UserAlreadyExistException("User already exists with phone number!");
        }
    }

    private void ensurePasswordsMatch(RegisterRequest registerRequest) {
        if (!registerRequest.password().equals(registerRequest.passwordConfirmation())) {
            throw new BadCredentialsException("Passwords do not match");
        }
    }

    private User findByEmail(String email){
        return userRepository.findByEmail(email)
            .orElseThrow( () -> new UserNotFoundException("User not found with email!"));
    }

    private User findById(UUID id){
        return userRepository.findById(id)
            .orElseThrow( () -> new UserNotFoundException("User not found with id!"));
    }
}
