package com.kLagan.challenge.infraestructure.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import com.kLagan.challenge.domain.model.AssetUploadEvent;

import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaConfig {
    
    @Bean
    public ReactiveKafkaProducerTemplate<String, AssetUploadEvent> reactiveKafkaProducerTemplate() {
        Map<String, Object> props = Map.of(
            "bootstrap.servers", "localhost:9092",
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer",
            "acks", "all",
            "retries", 3
        );
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }
}