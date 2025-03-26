// src/main/java/com/kLagan/challenge/domain/model/Asset.java
package com.kLagan.challenge.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assets")
public class Asset {
    @Id
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private String uploadStatus;
    private String location;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private Map<String, String> metadata;
}