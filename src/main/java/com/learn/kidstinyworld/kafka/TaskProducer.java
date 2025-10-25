package com.learn.kidstinyworld.kafka;

import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskProducer {

    // KafkaTemplate Spring terefinden DI edilir (KafkaConfig-den sonra)
    private final KafkaTemplate<String, TaskCompletedEvent> kafkaTemplate;

    // Kafka-ya gondereceyimiz TOPIC-in adi
    private static final String TOPIC_NAME = "task_completion_events";

    // AssignmentService bu metodu cagiracaq
    public void sendCompletedEvent(TaskCompletedEvent event) {
        // Asinxron olaraq mesaji Kafka TOPIC-ine gonder
        kafkaTemplate.send(TOPIC_NAME, event);
        System.out.println("-> KAFKA PRODUCER: Tamamlanma hadisesi gonderildi. Child ID: " + event.getChildId());
    }
}