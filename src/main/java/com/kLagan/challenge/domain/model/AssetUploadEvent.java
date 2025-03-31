package com.kLagan.challenge.domain.model;

import java.util.Arrays;

public record AssetUploadEvent(
    String assetId,
    byte[] fileContent,
    String fileLocation
) {
    public String getAssetId() {
        return assetId;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public AssetUploadEvent(String assetId, byte[] fileContent) {
        this(assetId, fileContent, null);
    }

    public AssetUploadEvent(String assetId, String fileLocation) {
        this(assetId, null, fileLocation);
    }

    @Override
    public String toString() {
        return "AssetUploadEvent{" +
            "assetId='" + assetId + '\'' +
            ", fileContentSize=" + (fileContent != null ? fileContent.length : 0) +
            ", fileLocation='" + fileLocation + '\'' +
            '}';
    }
}