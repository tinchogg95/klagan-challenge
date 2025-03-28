package com.kLagan.challenge.infraestructure.api;

import com.kLagan.challenge.application.service.AssetService;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mgmt/1/assets")
public class AssetController {
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/actions/upload")
    public Mono<ResponseEntity<AssetFileUploadResponse>> uploadAssetFile(
            @RequestBody AssetFileUploadRequest request) {
        return assetService.handleUpload(request)
            .map(assetId -> ResponseEntity.accepted()
                .body(new AssetFileUploadResponse(assetId)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Asset>>> getAssetsByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateEnd,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String filetype,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        Flux<Asset> assets = assetService.searchAssets(
            uploadDateStart,
            uploadDateEnd,
            filename,
            filetype,
            sortDirection
        );
        
        return Mono.just(ResponseEntity.ok(assets));
    }
}