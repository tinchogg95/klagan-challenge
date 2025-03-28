package com.kLagan.challenge.application.port;

public interface AssetProcessingService {
    void processUploadedAsset(String assetId, byte[] fileContent);
    void markAsFailed(String assetId, String errorMessage);
}