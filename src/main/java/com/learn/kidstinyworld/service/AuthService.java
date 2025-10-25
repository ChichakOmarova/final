package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.dto.ParentRegistrationRequest;
import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.enums.UserRole;
import com.learn.kidstinyworld.exception.UserAlreadyExistsException;
import com.learn.kidstinyworld.repository.ParentRepository;
import com.learn.kidstinyworld.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ParentDetailsService parentDetailsService;
    private final JwtUtil jwtUtil;

    // -----------------------------------------------------------
    // 1. Qeydiyyat (Registration) Mentiqi
    // -----------------------------------------------------------
    public Parent registerParent(ParentRegistrationRequest request) {

        // 1. İstifadəçinin artıq olub-olmadığını yoxla
        if (parentRepository.findByUsername(request.getUsername()) != null) {
            throw new UserAlreadyExistsException("Bu istifadəçi adı (" + request.getUsername() + ") artıq mövcuddur.");
        }

        // 2. Yeni Parent obyekti yarat
        Parent newParent = Parent.builder()
                .username(request.getUsername())
                // Sifreni HASH et!
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(UserRole.USER)
                .build();

        // 3. DB-ya yaddaşda saxla
        return parentRepository.save(newParent);
    }

    // -----------------------------------------------------------
    // 2. Login Mentiqi
    // -----------------------------------------------------------
    public String authenticateUser(String username, String password) {

        // 1. Spring Security vasitesiyle istifadecini tesdiqle
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2. ParentDetailsService-den istifadecini getir (Eger tesdiqlenibse)
        final UserDetails userDetails = parentDetailsService.loadUserByUsername(username);

        // 3. Yeni JWT Tokeni yarat
        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt; // Controller-e tokeni qaytar
    }
}