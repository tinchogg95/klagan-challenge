// src/main/java/com/kLagan/challenge/application/service/AssetService.java
package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.domain.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository assetRepository;
    private final AssetPublisher assetPublisher;

    public Mono<Asset> uploadAsset(FilePart filePart, String uploadedBy, Map<String, String> metadata) {
        return filePart.content()
            .collectList()
            .flatMap(dataBuffers -> {
                // Calcular tamaño del archivo
                long size = dataBuffers.stream()
                    .mapToInt(data -> data.asByteBuffer().remaining())
                    .sum();

                // Manejar metadata nula
                Map<String, String> finalMetadata = Optional.ofNullable(metadata).orElse(new HashMap<>());

                // Crear nuevo Asset
                Asset asset = Asset.builder()
                    .id(UUID.randomUUID())
                    .name(filePart.filename())
                    .type(filePart.headers().getContentType().toString())
                    .size(size)
                    .uploadStatus("PENDING")
                    .uploadedAt(LocalDateTime.now())
                    .uploadedBy(uploadedBy)
                    .metadata(finalMetadata)
                    .build();

                // Guardar y publicar
                return assetRepository.save(asset)
                    .flatMap(savedAsset -> 
                        assetPublisher.publishAsset(savedAsset)
                            .thenReturn(savedAsset)
                    );
            });
    }

    public Mono<Asset> getAssetById(UUID id) {
        return assetRepository.findById(id);
    }

    public Mono<Page<Asset>> searchAssets(
        String name, 
        String type, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    ) {
        // Procesar parámetros opcionales
        String processedName = Optional.ofNullable(name).orElse("");
        String processedType = Optional.ofNullable(type).orElse("");
        LocalDateTime processedStartDate = Optional.ofNullable(startDate).orElse(LocalDateTime.MIN);
        LocalDateTime processedEndDate = Optional.ofNullable(endDate).orElse(LocalDateTime.MAX);

        // Obtener assets paginados
        Flux<Asset> assetsFlux = assetRepository
            .findByNameContainingIgnoreCaseAndTypeAndUploadedAtBetween(
                processedName,
                processedType,
                processedStartDate,
                processedEndDate,
                pageable
            );

        // Obtener conteo total
        Mono<Long> countMono = assetRepository
            .countByNameContainingIgnoreCaseAndTypeAndUploadedAtBetween(
                processedName,
                processedType,
                processedStartDate,
                processedEndDate
            );

        // Combinar resultados
        return Mono.zip(assetsFlux.collectList(), countMono)
            .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
}