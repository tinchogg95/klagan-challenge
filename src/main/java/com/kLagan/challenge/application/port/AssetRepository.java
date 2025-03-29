package com.kLagan.challenge.application.port;

import com.kLagan.challenge.domain.model.Asset;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;

public interface AssetRepository {
    Mono<Asset> save(Asset asset);
    Flux<Asset> search(
        LocalDateTime uploadDateStart,
        LocalDateTime uploadDateEnd,
        String filename,
        String filetype,
        String status,
        String sortDirection
    );
    Mono<Asset> findById(String id);
    Mono<Void> updateStatus(String assetId, String status);
    Mono<Void> updateUrl(String assetId, String url);
    
}