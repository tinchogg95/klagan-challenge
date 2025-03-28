package com.kLagan.challenge.infraestructure.api.dto;

public class AssetFileUploadRequest {
    private String filename;
    private String encodedFile;
    private String contentType;
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getEncodedFile() {
        return encodedFile;
    }
    public void setEncodedFile(String encodedFile) {
        this.encodedFile = encodedFile;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

  
}