package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;
import com.burakyapici.library.common.util.AuthServiceTestUtil;
import com.burakyapici.library.common.util.UserServiceTestUtil;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.security.UserDetailsImpl;
import com.burakyapici.library.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Given valid login request, when login, then return login response with token")
    public void givenValidLoginRequest_whenLogin_thenReturnLoginResponseWithToken() {
        LoginRequest loginRequest = AuthServiceTestUtil.createSampleLoginRequest();
        UserDetailsImpl userDetails = AuthServiceTestUtil.createSampleUserDetails();
        String expectedToken = "valid.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateAccessToken(userDetails.getUsername())).thenReturn(expectedToken);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(expectedToken, response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(userDetails.getUsername(), response.email());
    }

    @Test
    @DisplayName("Given invalid credentials, when login, then throw BadCredentialsException")
    public void givenInvalidCredentials_whenLogin_thenThrowBadCredentialsException() {
        LoginRequest loginRequest = AuthServiceTestUtil.createSampleLoginRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Given valid register request, when register, then return register response with token")
    public void givenValidRegisterRequest_whenRegister_thenReturnRegisterResponseWithToken() {
        RegisterRequest registerRequest = AuthServiceTestUtil.createSampleRegisterRequest();
        User createdUser = UserServiceTestUtil.createSampleUserWithId(UUID.randomUUID());
        String expectedToken = "valid.jwt.token";

        when(userService.createUser(registerRequest)).thenReturn(createdUser);
        when(jwtService.generateAccessToken(createdUser.getEmail())).thenReturn(expectedToken);

        RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(createdUser.getId(), response.id());
        assertEquals(createdUser.getEmail(), response.email());
        assertEquals(createdUser.getFirstName(), response.firstName());
        assertEquals(createdUser.getLastName(), response.lastName());
        assertEquals(createdUser.getRole().getAuthority(), response.role());
        assertEquals(expectedToken, response.token());
    }
}
