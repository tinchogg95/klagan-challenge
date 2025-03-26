// src/main/java/com/kLagan/challenge/infrastructure/config/KafkaConfig.java
package com.kLagan.challenge.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import com.kLagan.challenge.domain.model.Asset;

import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Bean
    public ReactiveKafkaProducerTemplate<String, Asset> reactiveKafkaProducerTemplate() {
        Map<String, Object> props = Map.of(
            "bootstrap.servers", "localhost:9092",
            "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer"
        );
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }
}