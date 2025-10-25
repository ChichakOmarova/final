package com.learn.kidstinyworld.repository;

import com.learn.kidstinyworld.entity.Assignment;
import com.learn.kidstinyworld.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Bir uşağın verilmiş statusdakı (PENDING və ya COMPLETED) tapşırıqlarını gətirir
    List<Assignment> findByChildIdAndStatus(Long childId, Status status);

    // Kafka Consumer-de lazim olacaq: tamamlanmamış tapşırıqları silmək/statusunu dəyişmək üçün
    // Optional<Assignment> findByIdAndStatus(Long assignmentId, Status status);
}