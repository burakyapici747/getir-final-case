package com.burakyapici.library.service.impl;

import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.AuthService;
import com.burakyapici.library.service.JwtService;
import com.burakyapici.library.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthServiceImpl(
        JwtService jwtService,
        UserService userService,
        AuthenticationManager authenticationManager
    ) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
                )
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(userDetails.getUsername());

            return new LoginResponse(
                accessToken,
                "Bearer",
                3600,
                userDetails.getEmail()
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        User newUser = userService.createUser(registerRequest);

        return new RegisterResponse(
            newUser.getId(),
            newUser.getEmail(),
            newUser.getFirstName(),
            newUser.getLastName(),
            newUser.getRole().getAuthority(),
            jwtService.generateAccessToken(newUser.getEmail())
        );
    }
}
