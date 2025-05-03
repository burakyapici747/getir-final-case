package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.repository.UserRepository;
import com.burakyapici.library.exception.UserAlreadyExistException;
import com.burakyapici.library.exception.UserNotFoundException;
import com.burakyapici.library.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserByEmail(String email) {
        return findByEmail(email);
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
}
