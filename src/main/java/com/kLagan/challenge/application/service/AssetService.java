package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);
    private static final String BASE64_PREFIX_REGEX = "^data:[^;]+;base64,";
    private static final String ASSET_STATUS_UPLOADED = "UPLOADED";
    private static final int MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    
    private final AssetRepository repository;
    private final AssetPublisher publisher;

    public AssetService(AssetRepository repository, AssetPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Mono<String> handleUpload(AssetFileUploadRequest request) {
        return Mono.fromCallable(() -> {
                validateRequest(request);
                String cleanBase64 = cleanBase64String(request.getEncodedFile());
                byte[] fileContent = Base64.getDecoder().decode(cleanBase64);
                validateFileSize(fileContent.length);
                return fileContent;
            })
            .onErrorMap(e -> {
                logger.error("Failed to process upload request", e);
                return new IllegalArgumentException("Invalid file content: " + e.getMessage());
            })
            .flatMap(fileContent -> createAndSaveAsset(request, fileContent));
    }

    private Mono<String> createAndSaveAsset(AssetFileUploadRequest request, byte[] fileContent) {
        String assetId = UUID.randomUUID().toString();
        
        Asset asset = new Asset();
        asset.setId(assetId);
        asset.setFilename(request.getFilename());
        asset.setContentType(request.getContentType());
        asset.setSize(fileContent.length);
        asset.setUploadDate(LocalDateTime.now());
        asset.setStatus(ASSET_STATUS_UPLOADED);
        
        return repository.save(asset)
            .doOnSuccess(savedAsset -> {
                logger.info("Asset saved successfully, ID: {}", assetId);
                publisher.publishAssetAsync(assetId, fileContent);
            })
            .thenReturn(assetId);
    }

    private void validateRequest(AssetFileUploadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getFilename() == null || request.getFilename().isBlank()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        if (request.getContentType() == null || request.getContentType().isBlank()) {
            throw new IllegalArgumentException("Content type cannot be empty");
        }
        if (request.getEncodedFile() == null || request.getEncodedFile().isBlank()) {
            throw new IllegalArgumentException("File content cannot be empty");
        }
    }

    private void validateFileSize(int fileSize) {
        if (fileSize > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + MAX_FILE_SIZE_BYTES + " bytes");
        }
    }

    private String cleanBase64String(String encodedFile) {
        // Remove data URL prefix if present
        String clean = encodedFile.replaceFirst(BASE64_PREFIX_REGEX, "");
        
        // Remove any whitespace
        clean = clean.replaceAll("\\s", "");
        
        // Validate Base64 characters
        if (!clean.matches("^[a-zA-Z0-9+/]*={0,2}$")) {
            throw new IllegalArgumentException("Invalid Base64 characters detected");
        }
        
        // Ensure proper padding
        switch (clean.length() % 4) {
            case 1:
                throw new IllegalArgumentException("Invalid Base64 length");
            case 2:
                clean += "==";
                break;
            case 3:
                clean += "=";
                break;
        }
        
        return clean;
    }

    public Flux<Asset> searchAssets(
        LocalDateTime uploadDateStart,
        LocalDateTime uploadDateEnd,
        String filename,
        String filetype,
        String status,
        String sortDirection) {
        
        return repository.search(
            uploadDateStart,
            uploadDateEnd,
            filename,
            filetype,
            status,
            sortDirection
        );
    }
}