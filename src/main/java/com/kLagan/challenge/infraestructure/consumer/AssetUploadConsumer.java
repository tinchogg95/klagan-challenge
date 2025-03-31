package com.kLagan.challenge.infraestructure.consumer;

import com.kLagan.challenge.domain.model.AssetUploadEvent;
import com.kLagan.challenge.application.port.AssetProcessingService;
import com.kLagan.challenge.application.util.AssetConstants;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AssetUploadConsumer {

    private final AssetProcessingService processingService;

    public AssetUploadConsumer(AssetProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = AssetConstants.KAFKA_TOPIC_ASSET_UPLOADS)
    public void consume(AssetUploadEvent event) {
        try {
            if (event.fileContent() != null) {
                processingService.processUploadedAsset(event.assetId(), event.fileContent());
            } else {
                processingService.markAsFailed(event.assetId(), AssetConstants.ERROR_NULL_FILE_CONTENT);
            }
        } catch (Exception e) {
            processingService.markAsFailed(event.assetId(), AssetConstants.ERROR_INTERNAL_PREFIX + e.getMessage());
        }
    }
}
