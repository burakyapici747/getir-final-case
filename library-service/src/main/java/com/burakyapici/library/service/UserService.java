package com.burakyapici.library.service;

import com.burakyapici.library.api.dto.request.RegisterRequest;
import com.burakyapici.library.domain.model.User;

public interface UserService {
    User getUserByEmail(String email);
    User createUser(RegisterRequest registerRequest);
}
