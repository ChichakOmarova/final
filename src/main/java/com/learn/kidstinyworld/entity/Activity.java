package com.learn.kidstinyworld.entity;

import com.learn.kidstinyworld.enums.ActivityCategory;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String title;

    private String description;

    // Hərəkət növü (COLORING, MATH, WORLD_VIEW)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityCategory category;

    // Bu tapşırığı bitirdikdə uşağın qazanacağı xal
    @Column(nullable = false)
    private Integer pointsValue;

    // Tapşırığın icrası üçün təxmini vaxt (Saat aralığı nəzarəti üçün nəzəri baza)
    private Integer estimatedDurationMinutes;
}