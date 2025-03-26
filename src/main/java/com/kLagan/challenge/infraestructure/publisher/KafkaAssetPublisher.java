// src/main/java/com/kLagan/challenge/infrastructure/publisher/KafkaAssetPublisher.java
package com.kLagan.challenge.infraestructure.publisher;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.domain.model.Asset;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KafkaAssetPublisher implements AssetPublisher {
    
    private final ReactiveKafkaProducerTemplate<String, Asset> kafkaTemplate;
    private static final String TOPIC = "asset-upload-events";

    @Override
    public Mono<Void> publishAsset(Asset asset) {
        return kafkaTemplate.send(TOPIC, asset.getId().toString(), asset)
                .then();
    }
}