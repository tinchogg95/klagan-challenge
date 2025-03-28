// src/main/java/com/kLagan/challenge/domain/model/Asset.java
package com.kLagan.challenge.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assets")
public class Asset {
    @Id
    private String id;
    private String filename;
    private String contentType;
    private String url;
    private long size;
    private LocalDateTime uploadDate;
    private String status; // "UPLOADED", "PROCESSING", "PUBLISHED", "FAILED"
}