package com.learn.kidstinyworld.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private Long id;
    private String email;
}