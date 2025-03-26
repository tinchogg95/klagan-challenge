// src/main/java/com/klagan/assetservice/infrastructure/api/dto/AssetResponse.java
package com.kLagan.challenge.infraestructure.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.kLagan.challenge.domain.model.Asset;

@Data
public class AssetResponse {
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private String uploadStatus;
    private String location;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private Map<String, String> metadata;

    public static AssetResponse fromDomain(Asset asset) {
        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setName(asset.getName());
        response.setType(asset.getType());
        response.setSize(asset.getSize());
        response.setUploadStatus(asset.getUploadStatus());
        response.setLocation(asset.getLocation());
        response.setUploadedAt(asset.getUploadedAt());
        response.setUploadedBy(asset.getUploadedBy());
        response.setMetadata(asset.getMetadata());
        return response;
    }
}

