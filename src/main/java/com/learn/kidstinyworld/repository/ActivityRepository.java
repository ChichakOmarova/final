package com.learn.kidstinyworld.repository;

import com.learn.kidstinyworld.entity.Activity;
import com.learn.kidstinyworld.enums.ActivityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Tapşırıq növünə görə filtrləmə (COLORING, MATH və s.)
    List<Activity> findAllByCategory(ActivityCategory category);
}