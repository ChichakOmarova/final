package com.learn.kidstinyworld.repository;

import com.learn.kidstinyworld.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Entity Class, ID Tipi>
public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Spring bu adlandırmaya baxaraq avtomatik olaraq:
    // SELECT * FROM parents WHERE username = ?
    // sorğusunu yaradır.
    Parent findByUsername(String username);
}