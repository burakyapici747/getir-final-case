package com.burakyapici.library.common.util;

import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;
import com.burakyapici.library.domain.enums.Role;
import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AuthServiceTestUtil {
    
    public static LoginRequest createSampleLoginRequest() {
        return new LoginRequest("user@example.com", "password123");
    }
    
    public static RegisterRequest createSampleRegisterRequest() {
        return new RegisterRequest(
                "newuser@example.com",
                "password123",
                "password123",
                "John",
                "Doe",
                "+905551234567",
                "123 Main St, Anytown"
        );
    }
    
    public static LoginResponse createSampleLoginResponse(String email) {
        return new LoginResponse(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.signature",
                "Bearer",
                3600,
                email
        );
    }
    
    public static RegisterResponse createSampleRegisterResponse(User user, String token) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                token
        );
    }
    
    public static UserDetailsImpl createSampleUserDetails() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        String password = "encodedPassword";
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(Role.PATRON.name())
        );
        
        return new UserDetailsImpl(userId, email, password, authorities);
    }
}
