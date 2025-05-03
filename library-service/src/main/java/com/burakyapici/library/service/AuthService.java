package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.LoginRequest;
import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.api.dto.response.LoginResponse;
import com.burakyapici.library.api.dto.response.RegisterResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);
}
