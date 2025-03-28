package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetProcessingService;
import com.kLagan.challenge.application.port.AssetRepository;
import org.springframework.stereotype.Service;

@Service
public class AssetProcessingServiceImpl implements AssetProcessingService {

    private final AssetRepository assetRepository;

    public AssetProcessingServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public void processUploadedAsset(String assetId, byte[] fileContent) {
        // 1. Aquí iría la lógica para subir el archivo al sistema final
        // (ej: Amazon S3, Google Cloud Storage, etc.)
        System.out.println("Procesando asset: " + assetId);
        
        // 2. Actualizar el estado en MongoDB
        assetRepository.updateStatus(assetId, "PROCESSED")
            .doOnSuccess(__ -> System.out.println("Estado actualizado a PROCESSED"))
            .subscribe();
        
        // 3. Opcional: Guardar la URL del archivo en el sistema final
        // assetRepository.updateUrl(assetId, "https://storage.example.com/"+assetId);
    }

    @Override
    public void markAsFailed(String assetId, String errorMessage) {
        System.err.println("Error procesando asset " + assetId + ": " + errorMessage);
        assetRepository.updateStatus(assetId, "FAILED: " + errorMessage)
            .subscribe();
    }
}