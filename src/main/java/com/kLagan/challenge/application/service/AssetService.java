package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class AssetService {
    private static final String BASE64_PREFIX = "data:image/jpeg;base64,";
    private static final String ASSET_STATUS_UPLOADED = "UPLOADED";
    
    private final AssetRepository repository;
    private final AssetPublisher publisher;

    public AssetService(AssetRepository repository, AssetPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Mono<String> handleUpload(AssetFileUploadRequest request) {
        return Mono.fromCallable(() -> {
                String cleanBase64 = cleanBase64String(request.getEncodedFile());
                return Base64.getDecoder().decode(cleanBase64);
            })
            .onErrorMap(e -> new IllegalArgumentException("Invalid Base64 content", e))
            .flatMap(fileContent -> {
                String assetId = UUID.randomUUID().toString();
                
                Asset asset = new Asset();
                asset.setId(assetId);
                asset.setFilename(request.getFilename());
                asset.setContentType(request.getContentType());
                asset.setSize(fileContent.length);
                asset.setUploadDate(LocalDateTime.now());
                asset.setStatus(ASSET_STATUS_UPLOADED);
                
                return repository.save(asset)
                    .doOnSuccess(savedAsset -> publisher.publishAssetAsync(assetId, fileContent))
                    .thenReturn(assetId);
            });
    }

    public Flux<Asset> searchAssets(
        LocalDateTime uploadDateStart,
        LocalDateTime uploadDateEnd,
        String filename,
        String filetype,
        String sortDirection) {
        
        return repository.search(
            uploadDateStart,
            uploadDateEnd,
            filename,
            filetype,
            sortDirection
        );
    }

    private String cleanBase64String(String encodedFile) {
        if (encodedFile == null || encodedFile.isBlank()) {
            throw new IllegalArgumentException("Base64 content cannot be empty");
        }
        
        // Remove data URL prefix if present
        String clean = encodedFile.startsWith(BASE64_PREFIX) 
            ? encodedFile.substring(BASE64_PREFIX.length())
            : encodedFile;
            
        // Remove any whitespace
        clean = clean.replaceAll("\\s", "");
        
        // Ensure proper padding
        while (clean.length() % 4 != 0) {
            clean += "=";
        }
        
        return clean;
    }
}