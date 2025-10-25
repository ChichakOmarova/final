package com.learn.kidstinyworld.kafka;

import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import com.learn.kidstinyworld.service.EmailService;
import com.learn.kidstinyworld.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatsConsumer {

    private final StatsService statsService;
    private final EmailService emailService;

    // application.yml-də təyin etdiyimiz Kafka TOPIC-i dinləyir
    @KafkaListener(topics = "${spring.kafka.topic.completion}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleTaskCompletion(TaskCompletedEvent event) {

        System.out.println("-> KAFKA CONSUMER: Yeni tamamlanma hadisesi qebul edildi: " + event.getAssignmentId());

        try {
            // 1. STATISTIKA: Bal hesablama və streak yenilənməsi (Service Layer)
            statsService.updatePointsAndStats(event);

            // 2. EMAIL: Valideynə uğur haqqında bildiriş göndər (Asinxron)
            emailService.sendCompletionNotification(event.getParentId(), event);

        } catch (Exception e) {
            // Əgər DB-də (StatsService-də) və ya Mail-də səhv olarsa, log-a yazırıq.
            // Kritik qeyd: Həqiqi sistemdə bu mesajı DLQ-a (Dead Letter Queue) göndərmək lazımdır.
            System.err.println("KAFKA IŞLENMESINDE XETA: " + e.getMessage());
        }
    }
}