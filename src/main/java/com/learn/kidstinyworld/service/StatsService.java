package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import com.learn.kidstinyworld.entity.Child;
import com.learn.kidstinyworld.entity.DailyStats;
import com.learn.kidstinyworld.exception.ResourceNotFoundException;
import com.learn.kidstinyworld.repository.ChildRepository;
import com.learn.kidstinyworld.repository.DailyStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final ChildRepository childRepository;
    private final DailyStatsRepository dailyStatsRepository;
    // private final EmailService emailService; // Mail service de burda istifade olunacaq

    // -----------------------------------------------------------
    // 1. Kafka Consumer terefinden cagirilir (Asinxron bal hesablama)
    // -----------------------------------------------------------
    @Transactional // Database emeliyyatlari üçün vacibdir
    public void updatePointsAndStats(TaskCompletedEvent event) {

        // 1. Uşaqı tap
        Child child = childRepository.findById(event.getChildId())
                .orElseThrow(() -> new ResourceNotFoundException("Uşaq", "id", event.getChildId()));

        // 2. Total Points yenile
        child.setTotalPoints(child.getTotalPoints() + event.getPointsAwarded());
        childRepository.save(child);

        // 3. Gündəlik Statistikanı yenilə
        LocalDate today = LocalDate.now();
        DailyStats dailyStats = dailyStatsRepository.findByChildIdAndDate(child.getId(), today);

        if (dailyStats == null) {
            dailyStats = DailyStats.builder()
                    .child(child)
                    .date(today)
                    .build();
        }

        dailyStats.setDailyPoints(dailyStats.getDailyPoints() + event.getPointsAwarded());
        dailyStats.setIsActiveDay(true);
        dailyStats.setCompletedTaskCount(dailyStats.getCompletedTaskCount() + 1);
        dailyStatsRepository.save(dailyStats);

        // 4. Email gonderilmesini aktiv et
        // emailService.sendCompletionNotification(event.getParentId(), event);

        System.out.println("-> STATS SERVICE: Bal hesablandi ve Statistika yenilendi.");
    }

    // -----------------------------------------------------------
    // 2. Gündəlik Qalibi Tapmaq (StatsController çağırır)
    // -----------------------------------------------------------
    public List<DailyStats> getDailyWinner(int limit) {
        LocalDate today = LocalDate.now();
        // Repository-deki metodu istifade edirik
        return dailyStatsRepository.findByDateOrderByDailyPointsDesc(today);
    }

    // -----------------------------------------------------------
    // 3. Flame Streak Hesablanması (Scheduler çağırır)
    // -----------------------------------------------------------
    public void calculateFlameStreak(Long childId) {
        // Bu hissə SchedulerConfig-də 00:01-də avtomatik işə düşəcək
        System.out.println("-> SCHEDULER: Flame streak hesablama simulyasiyası.");
    }
}