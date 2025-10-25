package com.learn.kidstinyworld.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_stats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Bu entity-de bir uşaq üçün bir tarixdə yalnız bir qeyd olmalıdır
@IdClass(DailyStatsId.class)
public class DailyStats {

    // Birgə Açar (Composite Key) 1: Uşaq ID-si
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // Birgə Açar (Composite Key) 2: Tarix
    @Id
    @Column(nullable = false)
    private LocalDate date;

    // Gündəlik qazanılan xal (Gündəlik qalib üçün)
    @Column(nullable = false)
    private Integer dailyPoints = 0;

    // Həmin gün ən azı bir tapşırıq tamamlanıbsa 'true' olur (Streak üçün)
    @Column(nullable = false)
    private Boolean isActiveDay = false;

    // Əlavə: Gündəlik tamamlanan tapşırıqların sayı
    private Integer completedTaskCount = 0;
}