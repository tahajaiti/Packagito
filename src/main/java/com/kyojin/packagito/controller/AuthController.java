package com.kyojin.packagito.controller;

import com.kyojin.packagito.dto.request.LoginRequest;
import com.kyojin.packagito.dto.request.RegisterRequest;
import com.kyojin.packagito.dto.response.AuthResponse;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody  RegisterRequest request) {
        UserDTO userDTO = authService.register(request);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> profile() {
        UserDTO userDTO = authService.profile();
        return ResponseEntity.ok(userDTO);
    }

}
