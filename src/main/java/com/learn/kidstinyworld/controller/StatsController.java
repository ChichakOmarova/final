package com.learn.kidstinyworld.controller;

import com.learn.kidstinyworld.entity.DailyStats;
import com.learn.kidstinyworld.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // 1. Gündəlik Qalibləri Gətirmək (Ən çox xal toplayanlar)
    // GET /api/stats/daily-winners?limit=10
    @GetMapping("/daily-winners")
    public ResponseEntity<List<DailyStats>> getDailyWinners(
            // Nece neferin siyahisinin gosterilmesini teyin edir
            @RequestParam(defaultValue = "10") int limit) {

        List<DailyStats> winners = statsService.getDailyWinner(limit);
        return ResponseEntity.ok(winners);
    }

    // 2. Hal-hazırda sistemdə qeyd olunan streak-i yoxlamaq üçün (Sadə Read)
    // GET /api/stats/child-streak/{childId}
    // Qeyd: Bu metod ChildService-den istifade ederek Child entity-sini gətirib flameStreak-i gostere biler

    // 3. Əlavə tapşırıq: Uşağın bu gün neçə bal yığdığını göstərmək
    // GET /api/stats/child-today/{childId}
}