package com.kLagan.challenge.infraestructure.api.dto;

public class AssetFileUploadResponse {
    private String id;
    private String errorMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AssetFileUploadResponse() {}
    
    public AssetFileUploadResponse(String id) {
        this.id = id;
    }

}