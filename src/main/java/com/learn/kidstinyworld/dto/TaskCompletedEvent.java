package com.learn.kidstinyworld.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalTime;

// Serializable olmalidir ki, Kafka ile ötürülə bilsin
@Data
public class TaskCompletedEvent implements Serializable {

    private Long assignmentId;
    private Long childId;
    private Long parentId; // Email gonderilmesi ucun vacibdir
    private Integer pointsAwarded;
    private LocalTime completionTime;
    private Boolean isCompleted = true; // Tamamlanma statusu
}