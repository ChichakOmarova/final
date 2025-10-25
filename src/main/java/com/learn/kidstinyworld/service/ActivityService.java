package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.entity.Activity;
import com.learn.kidstinyworld.enums.ActivityCategory;
import com.learn.kidstinyworld.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    // -----------------------------------------------------------
    // 1. Yeni Fəaliyyət Yaratmaq (Databazani doldurmaq üçün)
    // -----------------------------------------------------------
    @CacheEvict(value = "popularActivities", allEntries = true) // Yeni aktivlik yarananda cache-i temizle
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    // -----------------------------------------------------------
    // 2. Bütün Fəaliyyətləri Gətirmək (Controller çağırır)
    // -----------------------------------------------------------
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    // -----------------------------------------------------------
    // 3. Kateqoriyaya görə Fəaliyyətləri Gətirmək
    // -----------------------------------------------------------
    public List<Activity> getActivitiesByCategory(ActivityCategory category) {
        return activityRepository.findAllByCategory(category);
    }

    // -----------------------------------------------------------
    // 4. CACHING: Populyar Fəaliyyətləri Sürətli Gətirmək
    // -----------------------------------------------------------
    /**
     * @Cacheable Annotasiyası: Metodun nəticəsini "popularActivities" cache-də saxlayir.
     */
    @Cacheable(value = "popularActivities", key = "'allPopular'")
    public List<Activity> getPopularActivities() {
        // Bu hissə YALNIZ cache boşdursa işləyir (Məs: 5 deqiqeden bir)

        System.out.println("-> DB/Hesablamadan Populyar Fəaliyyətlər Gətirilir (Yavaş Əməliyyat)...");

        // Simulyasiya: Çox vaxt alan bir DB sorğusu və ya hesablama
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Həqiqi Layihədə: Məsələn, ən çox təyin edilmiş 5 Activity gətirilir
        return activityRepository.findAll().stream()
                .limit(5)
                .toList();
    }
}