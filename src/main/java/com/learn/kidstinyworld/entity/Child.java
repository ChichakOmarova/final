package com.learn.kidstinyworld.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "children")
@Getter
@Setter
@Builder // Obyekt yaratmagi asanlasdirir
@NoArgsConstructor
@AllArgsConstructor
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    private String avatarUrl; // Uşağın şəkli

    // Oyun Mexanikası
    private Integer totalPoints = 0; // Uşağın ümumi topladığı xal
    private Integer flameStreak = 0; // Ardıcıl işlədiyi günlərin sayı (Alov)

    // Əlaqə: Hər bir uşaq bir valideynə aiddir
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false) // DB-de xarici açar (Foreign Key)
    private Parent parent;

    // Optional: Child-Assignment əlaqəsi (lazım olsa əlavə edərik)
    // @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Assignment> assignments;
}