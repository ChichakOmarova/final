package com.learn.kidstinyworld.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ParentRegistrationRequest {

    @NotBlank(message = "İstifadəçi adı boş ola bilməz")
    @Size(min = 3, max = 50, message = "İstifadəçi adı 3-50 simvol olmalıdır")
    private String username;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə minimum 6 simvol olmalıdır")
    private String password;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Düzgün email formatı daxil edin")
    private String email;
}