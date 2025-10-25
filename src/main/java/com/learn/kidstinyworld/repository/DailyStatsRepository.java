package com.learn.kidstinyworld.repository;

import com.learn.kidstinyworld.entity.DailyStats;
import com.learn.kidstinyworld.entity.DailyStatsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatsRepository extends JpaRepository<DailyStats, DailyStatsId> {

    // Bir uşağın müəyyən bir tarixdəki statistikasını gətirmək
    DailyStats findByChildIdAndDate(Long childId, LocalDate date);

    // Gündəlik qalibi tapmaq üçün bu günün ən yüksək xallı qeydlərini sıralayırıq
    List<DailyStats> findByDateOrderByDailyPointsDesc(LocalDate date);
}