package com.learn.kidstinyworld.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "İstifadəçi adı tələb olunur")
    private String username;

    @NotBlank(message = "Şifrə tələb olunur")
    private String password;
}