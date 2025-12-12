package com.kyojin.packagito.service;

import com.kyojin.packagito.dto.request.LoginRequest;
import com.kyojin.packagito.dto.request.RegisterRequest;
import com.kyojin.packagito.dto.response.AuthResponse;
import com.kyojin.packagito.dto.response.UserDTO;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    UserDTO register(RegisterRequest request);

    UserDTO profile();
}
