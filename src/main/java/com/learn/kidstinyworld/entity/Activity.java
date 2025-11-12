package com.learn.kidstinyworld.entity;

import com.learn.kidstinyworld.enums.ActivityCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "activities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    private String description;

    // Hərəkət növü (COLORING, MATH, WORLD_VIEW)
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityCategory category;

    // Bu tapşırığı bitirdikdə uşağın qazanacağı xal
    @NotNull(message = "Points value is required")
    @Min(value = 0, message = "Points must be non-negative")
    @Column(nullable = false)
    private Integer pointsValue;

    // Tapşırığın icrası üçün təxmini vaxt (Saat aralığı nəzarəti üçün nəzəri baza)
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer estimatedDurationMinutes;
}