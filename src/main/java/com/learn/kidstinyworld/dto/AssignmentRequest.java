package com.learn.kidstinyworld.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentRequest {

    @NotNull(message = "Uşaq ID-si tələb olunur")
    private Long childId;

    @NotNull(message = "Fəaliyyət ID-si tələb olunur")
    private Long activityId;

    // Tapşırığın hansı vaxt aralığında başladığını da qeyd etmək olar
    // private java.time.LocalTime startTime;
}