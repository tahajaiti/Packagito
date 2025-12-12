package com.kyojin.packagito.service.impl;

import com.kyojin.packagito.core.exception.DuplicateResourceException;
import com.kyojin.packagito.core.exception.NotFoundException;
import com.kyojin.packagito.core.exception.UnauthorizedException;
import com.kyojin.packagito.core.exception.UserDisabledException;
import com.kyojin.packagito.dto.request.LoginRequest;
import com.kyojin.packagito.dto.request.RegisterRequest;
import com.kyojin.packagito.dto.response.AuthResponse;
import com.kyojin.packagito.dto.response.UserDTO;
import com.kyojin.packagito.entity.user.User;
import com.kyojin.packagito.mapper.AuthMapper;
import com.kyojin.packagito.repository.UserRepository;
import com.kyojin.packagito.security.JwtUtil;
import com.kyojin.packagito.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtUtil.generateJwt(authentication);

            User user = this.getUserByUsername(request.getUsername());

            log.info("User '{}' logged in successfully", request.getUsername());

            return authMapper.toAuthResponse(user, token);
        } catch (DisabledException e) {
            log.error("User account is disabled: {}", request.getUsername());
            throw new UserDisabledException("User account is disabled: " + request.getUsername());
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw new UnauthorizedException("Invalid credentials for user: " + request.getUsername());
        }
    }

    public UserDTO register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("Username '{}' is already taken", request.getUsername());
            throw new DuplicateResourceException("Username is already taken: " + request.getUsername());
        }

        User newUser = authMapper.toCarrier(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setActive(true);

        User savedUser = userRepository.save(newUser);

        log.info("User '{}' registered successfully", request.getUsername());

        return authMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        User user = this.getUserByUsername(authentication.getName());

        return authMapper.toDTO(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }
}
