package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetProcessingService;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.application.util.AssetConstants;
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
        log.info(AssetConstants.LOG_START_PROCESSING, assetId);

        uploadToStorage(assetId, fileContent)
            .flatMap(storageUrl -> {
                log.info(AssetConstants.LOG_UPLOAD_SUCCESS, assetId, storageUrl);
                return assetRepository.updateStatus(assetId, AssetConstants.STATUS_PROCESSED)
                    .then(assetRepository.updateUrl(assetId, storageUrl));
            })
            .onErrorResume(e -> {
                String errorMsg = AssetConstants.ERROR_UPLOAD_FAILURE + e.getMessage();
                log.error(AssetConstants.LOG_PROCESSING_ERROR, assetId, errorMsg, e);
                return assetRepository.updateStatus(assetId, AssetConstants.STATUS_FAILED_PREFIX + errorMsg);
            })
            .subscribe(
                result -> log.info(AssetConstants.LOG_PROCESSING_COMPLETED, assetId),
                error -> log.error(AssetConstants.ERROR_UNEXPECTED, error)
            );
    }

    @Override
    public void markAsFailed(String assetId, String errorMessage) {
        log.error(AssetConstants.LOG_MARK_FAILED, assetId, errorMessage);
        assetRepository.updateStatus(assetId, AssetConstants.STATUS_FAILED_PREFIX + errorMessage)
            .subscribe(
                result -> log.info(AssetConstants.LOG_UPDATE_FAILED_STATUS, assetId),
                error -> log.error(AssetConstants.LOG_UPDATE_FAILED_ERROR, error)
            );
    }

    private Mono<String> uploadToStorage(String assetId, byte[] fileContent) {
        return Mono.fromCallable(() -> {
            if (fileContent.length > 10_000_000) { 
                throw new RuntimeException(AssetConstants.ERROR_FILE_TOO_LARGE);
            }

            log.debug(AssetConstants.LOG_UPLOADING_FILE, assetId, fileContent.length);
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                System.err.println("Sleep interrumpido");
            }
            return AssetConstants.STORAGE_URL_BASE + assetId;
        });
    }
}
