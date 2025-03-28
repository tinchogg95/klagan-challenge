package com.kLagan.challenge.application.port;

public interface AssetPublisher {
    void publishAssetAsync(String assetId, byte[] fileContent);
    void publishAssetAsync(String assetId, String fileLocation);
}