package com.learn.kidstinyworld.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.kidstinyworld.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "parents")
@Getter
@Setter
@Builder // Yeni elave etdik: obyekt yaratmagi asanlasdirir
@NoArgsConstructor
@AllArgsConstructor
public class Parent implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Valideynin unikal istifadeci adi

    @Column(nullable = false)
    private String password; // Hashlenmis sifre

    @Column(unique = true, nullable = false)
    private String email;    // Email bildirisleri ucun vacibdir

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // USER veya ADMIN

    // Parent-Child Elaqesi: Bir valideynin bir nece usagi var
    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Child> children;


    // -------------------------------------------------------------------
    // Spring Security UserDetails Metodlari
    // -------------------------------------------------------------------

    // Default olaraq hamisini 'true' qoyuruq.
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

}