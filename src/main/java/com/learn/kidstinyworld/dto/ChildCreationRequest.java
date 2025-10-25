package com.learn.kidstinyworld.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChildCreationRequest {

    @NotBlank(message = "Uşağın adı tələb olunur")
    private String name;

    @NotNull(message = "Uşağın yaşı tələb olunur")
    @Min(value = 3, message = "Platforma minimum 3 yaşlı uşaqlar üçündür")
    private Integer age;

    // AvatarUrl optional ola biler
    private String avatarUrl;
}