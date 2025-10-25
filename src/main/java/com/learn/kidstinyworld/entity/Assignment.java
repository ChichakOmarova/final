package com.learn.kidstinyworld.entity;

import com.learn.kidstinyworld.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assignment hansı uşağa aiddir (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // Assignment hansı fəaliyyəti təmsil edir (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private LocalDate assignedDate = LocalDate.now(); // Tapşırığın verilmə tarixi

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING; // Başlanğıc statusu

    // Tapşırığın tamamlama vaxtı (Valideyn düyməyə basanda qeyd olunur)
    private LocalTime completionTime;

    // Tapşırıq bitirildiyi zaman qazanılan xal (Məsələn, vaxtında bitirilməyə görə dəyişə bilər)
    private Integer pointsAwarded;
}