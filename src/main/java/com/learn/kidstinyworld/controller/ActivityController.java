package com.learn.kidstinyworld.controller;

import com.learn.kidstinyworld.dto.AssignmentRequest;
import com.learn.kidstinyworld.entity.Activity;
import com.learn.kidstinyworld.entity.Assignment;
import com.learn.kidstinyworld.enums.ActivityCategory;
import com.learn.kidstinyworld.service.ActivityService;
import com.learn.kidstinyworld.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final AssignmentService assignmentService;

    // 1. Fəaliyyətləri kateqoriyaya görə gətirmək VƏ bütün fəaliyyətləri gətirmək
    // GET /api/activities?category=MATH
    // GET /api/activities (butun aktivlikler)
    @GetMapping
    public ResponseEntity<List<Activity>> getActivitiesByCategory(@RequestParam(required = false) ActivityCategory category) {

        List<Activity> activities;
        if (category != null) {
            activities = activityService.getActivitiesByCategory(category);
        } else {
            // ActivityService-de olan butun aktivlikleri gətirən metoda düzgün çağırış
            activities = activityService.getAllActivities();
        }
        return ResponseEntity.ok(activities);
    }

    // 2. CACHING: Populyar Fəaliyyətləri Gətirmək
    // GET /api/activities/popular
    @GetMapping("/popular")
    public ResponseEntity<List<Activity>> getPopularActivities() {
        List<Activity> activities = activityService.getPopularActivities();
        return ResponseEntity.ok(activities);
    }

    // 3. Uşağa Tapşırıq Təyin Etmək (Valideyn tərəfindən)
    // POST /api/activities/assign
    @PostMapping("/assign")
    public ResponseEntity<Assignment> assignActivity(@Valid @RequestBody AssignmentRequest request) {
        Assignment newAssignment = assignmentService.assignTask(request);
        return new ResponseEntity<>(newAssignment, HttpStatus.CREATED);
    }

    // 4. Uşaq Tapşırığı Tamamlayır (Valideyn mobil tətbiqde düyməni basır)
    // POST /api/activities/complete/{assignmentId}
    @PostMapping("/complete/{assignmentId}")
    public ResponseEntity<String> completeActivity(@PathVariable Long assignmentId) {

        // Bu metod AssignmentService-de Kafka-ya hadisə (Event) göndərir
        assignmentService.completeTask(assignmentId);

        // Cavab tez qaytarılır, çünki əsas iş (Point/Email) arxa planda işləyir
        return ResponseEntity.ok("Tapşırıq tamamlama hadisəsi qeydə alındı. Bal hesablama prosesi arxa planda işləyir.");
    }
}