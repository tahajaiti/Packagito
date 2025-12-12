package com.kyojin.packagito.dto.response;

import com.kyojin.packagito.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private String username;
    private Role role;

    public static AuthResponse of(String token, String username, Role role) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .role(role)
                .build();
    }
}