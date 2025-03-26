package com.kLagan.challenge.infraestructure.api;

import com.kLagan.challenge.application.service.AssetService;
import com.kLagan.challenge.infraestructure.api.dto.AssetResponse;
import com.kLagan.challenge.infraestructure.api.dto.PageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<ResponseEntity<AssetResponse>> uploadAsset(
            @RequestPart("file") FilePart file,
            @RequestParam String uploadedBy,
            @RequestParam(required = false) Map<String, String> metadata) {
        
        return assetService.uploadAsset(file, uploadedBy, metadata)
                .map(asset -> ResponseEntity
                        .accepted()
                        .body(AssetResponse.fromDomain(asset)))
                .onErrorResume(e -> Mono.just(ResponseEntity
                        .badRequest()
                        .build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AssetResponse>> getAsset(@PathVariable UUID id) {
        return assetService.getAssetById(id)
                .map(asset -> ResponseEntity.ok(AssetResponse.fromDomain(asset)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<AssetResponse>>> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        
        return assetService.searchAssets(name, type, startDate, endDate, pageable)
                .map(page -> ResponseEntity.ok(PageResponse.fromPage(page, AssetResponse::fromDomain)))
                .onErrorResume(e -> Mono.just(ResponseEntity
                        .badRequest()
                        .build()));
    }
}