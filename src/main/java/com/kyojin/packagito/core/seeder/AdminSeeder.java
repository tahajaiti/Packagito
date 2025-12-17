package com.kyojin.packagito.core.seeder;

import com.kyojin.packagito.entity.enums.Role;
import com.kyojin.packagito.entity.user.Admin;
import com.kyojin.packagito.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.init-login}")
    private String adminUsername;

    @Value("${app.admin.init-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername(adminUsername)) {
            Admin admin = Admin.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Initial admin user '{}' created successfully", adminUsername);
        } else {
            log.info("Admin user '{}' already exists, skipping creation", adminUsername);
        }
    }
}