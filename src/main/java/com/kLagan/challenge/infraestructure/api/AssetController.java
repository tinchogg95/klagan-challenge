package com.kLagan.challenge.infraestructure.api;

import com.kLagan.challenge.application.service.AssetService;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/mgmt/1/assets")
public class AssetController {
    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);
    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/actions/upload")
    public Mono<ResponseEntity<AssetFileUploadResponse>> uploadAssetFile(
            @RequestBody AssetFileUploadRequest request) {
        return assetService.handleUpload(request)
            .map(assetId -> {
                AssetFileUploadResponse response = new AssetFileUploadResponse(assetId);
                return ResponseEntity.<AssetFileUploadResponse>accepted().body(response);
            })
            .onErrorResume(e -> {
                logger.error("Error processing upload", e);
                AssetFileUploadResponse errorResponse = new AssetFileUploadResponse();
                errorResponse.setErrorMessage(e.getMessage());
                return Mono.just(ResponseEntity
                    .<AssetFileUploadResponse>status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse));
            });
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Asset>>> getAssetsByFilter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime uploadDateEnd,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String filetype,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {
        
        return Mono.just(ResponseEntity.ok(assetService.searchAssets(
            uploadDateStart,
            uploadDateEnd,
            filename,
            filetype,
            status,
            sortDirection
        )));
    }
}