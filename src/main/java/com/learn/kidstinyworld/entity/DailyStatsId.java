package com.learn.kidstinyworld.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

// DailyStats üçün birləşmiş açarı təmsil edir
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatsId implements Serializable {
    // Sahə adları DailyStats entity-sindəki sahə adları ilə eyni olmalıdır
    private Long child;
    private LocalDate date;

    // JPA Composite Key üçün 'equals' və 'hashCode' metodları da tələb olunur
    // Lombok istifadə edirsinizsə, @EqualsAndHashCode annotasiyasını əlavə etmək olar.
    // Lakin bu sinfi Lombok-suz yazmaq daha təhlükəsizdir, ancaq burada sadəlik üçün saxlayırıq.

    // Qeyd: Bu sinfin icərisində Lombok @EqualsAndHashCode annotasiyasını əlavə etmək tövsiyyə olunur.
}