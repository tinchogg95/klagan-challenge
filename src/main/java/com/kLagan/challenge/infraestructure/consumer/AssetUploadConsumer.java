package com.kLagan.challenge.infraestructure.consumer;

import com.kLagan.challenge.domain.model.AssetUploadEvent;
import com.kLagan.challenge.application.port.AssetProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssetUploadConsumer {

    private final AssetProcessingService processingService;

    public AssetUploadConsumer(AssetProcessingService processingService) {
        this.processingService = processingService;
    }
    @KafkaListener(topics = "asset-uploads")
    public void consume(AssetUploadEvent event) {
        try {
            if (event.fileContent() != null) {
                processingService.processUploadedAsset(event.assetId(), event.fileContent());
            } else {
                processingService.markAsFailed(
                    event.assetId(), 
                    "Contenido del archivo es nulo"
                );
            }
        } catch (Exception e) {
            processingService.markAsFailed(
                event.assetId(), 
                "Error interno: " + e.getMessage()
            );
        }
    }
}