package com.example.school_management.config;

import com.example.school_management.model.User;
import com.example.school_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initAdminUser() {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);
                log.info("=== Đã tạo tài khoản admin mặc định: admin / admin123 ===");
            }
        };
    }
}
