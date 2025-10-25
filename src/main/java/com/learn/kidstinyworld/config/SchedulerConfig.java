package com.learn.kidstinyworld.config;

import com.learn.kidstinyworld.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling // Scheduler funksiyalarini aktiv edir
@RequiredArgsConstructor
public class SchedulerConfig {

    private final StatsService statsService;

    /**
     * Flame Streak Hesablanması (Alov Ardıcıllığı)
     * * Hər gecə saat 00:01-də işə düşür.
     * Bu CRON expression-i istifadə edir: saniye deqiqe saat gün_ayın_gunu ay günün_heftede_gunu
     * "0 1 0 * * *" = 0 saniye, 1 deqiqe, 0 saat (gecə 12), hər ayin hər günü, hər ay, hər həftə günü.
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void dailyFlameStreakCheck() {

        System.out.println("-> SCHEDULER: Gündəlik Alov Ardıcıllığının Yoxlanılması Başladı.");

        // StatsService-deki flame streak hesablama metodunu çağırırıq
        // Qeyd: Bu metod bütün uşaqlar üzərindən keçməli və hər uşaq üçün hesablama aparmalıdır.
        // Biz sadəlik üçün bura nümunə qoyuruq, realda bu metodda əlavə DB sorgulari olacaq.

        statsService.calculateFlameStreak(null);

        System.out.println("-> SCHEDULER: Alov Ardıcıllığının Yoxlanılması Bitdi.");
    }
}