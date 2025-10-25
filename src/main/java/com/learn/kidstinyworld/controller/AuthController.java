package com.learn.kidstinyworld.controller;

import com.learn.kidstinyworld.dto.JwtResponse;
import com.learn.kidstinyworld.dto.LoginRequest;
import com.learn.kidstinyworld.dto.ParentRegistrationRequest;
import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserDetailsService parentDetailsService;

    // 1. Valideynin Qeydiyyati (Registration)
    @PostMapping("/register")
    public ResponseEntity<?> registerParent(@Valid @RequestBody ParentRegistrationRequest request) {

        // @Valid annotasiyasi DTO-daki @NotBlank, @Email ve s. yoxlamalarini aktiv edir
        Parent parent = authService.registerParent(request);

        return new ResponseEntity<>("Valideyn " + parent.getUsername() + " uğurla qeydiyyatdan keçdi.", HttpStatus.CREATED);
    }

    // 2. Valideynin Login-i (Autentifikasiya)
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {

        // AuthService-de istifadecini tesdiqle ve JWT tokeni yarat
        String jwt = authService.authenticateUser(request.getUsername(), request.getPassword());

        // Token yarandisa, istifadeci detallarini getir
        final UserDetails userDetails = parentDetailsService.loadUserByUsername(request.getUsername());

        // Cavabi standart JwtResponse formatinda hazirla
        JwtResponse response = new JwtResponse(
                jwt,
                "Bearer",
                userDetails.getUsername(),
                ((Parent) userDetails).getId(),
                ((Parent) userDetails).getEmail()
        );

        return ResponseEntity.ok(response);
    }
}