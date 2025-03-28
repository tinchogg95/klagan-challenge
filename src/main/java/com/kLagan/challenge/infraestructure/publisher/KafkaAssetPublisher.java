package com.kLagan.challenge.infraestructure.publisher;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.domain.model.AssetUploadEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class KafkaAssetPublisher implements AssetPublisher {

    private static final String TOPIC = "asset-uploads";
    private final KafkaTemplate<String, AssetUploadEvent> kafkaTemplate;

    public KafkaAssetPublisher(KafkaTemplate<String, AssetUploadEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishAssetAsync(String assetId, byte[] fileContent) {
        AssetUploadEvent event = new AssetUploadEvent(assetId, fileContent);
        
        CompletableFuture<SendResult<String, AssetUploadEvent>> future = 
            kafkaTemplate.send(TOPIC, assetId, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.printf("""
                    [KAFKA] Evento publicado exitosamente!
                    Topic: %s
                    Key: %s
                    Offset: %d
                    Partition: %d
                    """, 
                    result.getRecordMetadata().topic(),
                    result.getProducerRecord().key(),
                    result.getRecordMetadata().offset(),
                    result.getRecordMetadata().partition());
            } else {
                System.err.println("[KAFKA] Error publicando evento: " + ex.getMessage());
            }
        });
    }

    @Override
    public void publishAssetAsync(String assetId, String fileLocation) {
        throw new UnsupportedOperationException("Publicación de archivos por ubicación no soportada aún.");
    }
}
