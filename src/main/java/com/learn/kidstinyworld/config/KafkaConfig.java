package com.learn.kidstinyworld.config;

import com.learn.kidstinyworld.dto.TaskCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // application.yml-den oxunur
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // -----------------------------------------------------------
    // 1. PRODUCER (Gonderici) Konfiqurasiyasi
    // -----------------------------------------------------------
    @Bean
    public ProducerFactory<String, TaskCompletedEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, TaskCompletedEvent> kafkaTemplate() {
        // TaskProducer-in teleb etdiyi obyekt budur
        return new KafkaTemplate<>(producerFactory());
    }

    // -----------------------------------------------------------
    // 2. CONSUMER (Alıcı) Konfiqurasiyasi
    // -----------------------------------------------------------
    @Bean
    public ConsumerFactory<String, TaskCompletedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // props.put(ConsumerConfig.GROUP_ID_CONFIG, "kidsworld-group"); // application.yml-de qeyd olunub
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JsonDeserializer<TaskCompletedEvent> deserializer = new JsonDeserializer<>(TaskCompletedEvent.class, false);
        deserializer.addTrustedPackages("com.learn.kidstinyworld.dto"); // Guvenilen paketi qeyd edir

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCompletedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}