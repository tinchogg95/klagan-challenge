// src/main/java/com/kLagan/challenge/application/port/AssetPublisher.java
package com.kLagan.challenge.application.port;

import com.kLagan.challenge.domain.model.Asset;
import reactor.core.publisher.Mono;

public interface AssetPublisher {
    Mono<Void> publishAsset(Asset asset);
}