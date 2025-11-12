package com.learn.kidstinyworld.config;

import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.enums.UserRole;
import com.learn.kidstinyworld.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Default user
        if (parentRepository.findByUsername("user") == null) {
            Parent defaultParent = Parent.builder()
                    .username("user")
                    .password(passwordEncoder.encode("password"))
                    .email("user@example.com")
                    .role(UserRole.USER)
                    .build();
            parentRepository.save(defaultParent);
            System.out.println("Default user created: user/password");
        }
        
        // Admin user
        if (parentRepository.findByEmail("admin@example.com") == null) {
            Parent adminParent = Parent.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(UserRole.ADMIN)
                    .build();
            parentRepository.save(adminParent);
            System.out.println("Admin user created: admin/admin123");
        }
    }
}
