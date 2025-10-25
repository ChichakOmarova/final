package com.learn.kidstinyworld.repository;

import com.learn.kidstinyworld.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    // Spring avtomatik yaradır:
    // SELECT * FROM children WHERE parent_id = ?
    List<Child> findAllByParentId(Long parentId);

    // Qeyd: Bu metod gündəlik qalibi tapmaq üçün gələcəkdə lazım olacaq.
    // List<Child> findTop10ByTotalPointsDesc();
}