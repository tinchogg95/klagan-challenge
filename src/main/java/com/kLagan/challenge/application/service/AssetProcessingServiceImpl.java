package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetProcessingService;
import com.kLagan.challenge.application.port.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AssetProcessingServiceImpl implements AssetProcessingService {

    private static final Logger log = LoggerFactory.getLogger(AssetProcessingServiceImpl.class);
    private final AssetRepository assetRepository;

    public AssetProcessingServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public void processUploadedAsset(String assetId, byte[] fileContent) {
        log.info("Iniciando procesamiento del asset: {}", assetId);
        
        //Try to upload asset 
        uploadToStorage(assetId, fileContent)
            .flatMap(storageUrl -> {
                //PROCESSED CASE
                log.info("Subida exitosa para asset {}, URL: {}", assetId, storageUrl);
                return assetRepository.updateStatus(assetId, "PROCESSED")
                    .then(assetRepository.updateUrl(assetId, storageUrl));
            })
            .onErrorResume(e -> {
                //FAILED CASE
                String errorMsg = "Error en subida a almacenamiento: " + e.getMessage();
                log.error("Error procesando asset {}: {}", assetId, errorMsg, e);
                return assetRepository.updateStatus(assetId, "FAILED: " + errorMsg);
            })
            .subscribe(
                result -> log.info("Procesamiento completado para asset {}", assetId),
                error -> log.error("Error inesperado en el flujo de procesamiento", error)
            );
    }

    @Override
    public void markAsFailed(String assetId, String errorMessage) {
        log.error("Marcando asset {} como fallido: {}", assetId, errorMessage);
        assetRepository.updateStatus(assetId, "FAILED: " + errorMessage)
            .subscribe(
                result -> log.info("Estado actualizado a FAILED para asset {}", assetId),
                error -> log.error("Error al actualizar estado a FAILED", error)
            );
    }

    /**
     * Método que simula la subida a almacenamiento en la nube
     * (Reemplazar con la implementación real para AWS S3, Google Cloud Storage, etc.)
     */
    private Mono<String> uploadToStorage(String assetId, byte[] fileContent) {
        return Mono.fromCallable(() -> {
            //simulating an error
            if (fileContent.length > 10_000_000) { 
                throw new RuntimeException("Tamaño de archivo excede el límite permitido");
            }

            
            //simulating upload
            log.debug("Subiendo archivo {} ({} bytes) a almacenamiento...", assetId, fileContent.length);
            try {
                Thread.sleep(5_000); // simulating asyncronous uploading
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                System.err.println("Sleep interrumpido");
            }
            //return simulated url
            return "https://storage.example.com/files/" + assetId;
        });
    }
}