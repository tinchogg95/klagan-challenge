// src/main/java/com/kLagan/challenge/application/port/AssetRepository.java
package com.kLagan.challenge.application.port;

import com.kLagan.challenge.domain.model.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AssetRepository extends ReactiveMongoRepository<Asset, UUID> {
    
    // Método para búsqueda paginada
    Flux<Asset> findByNameContainingIgnoreCaseAndTypeAndUploadedAtBetween(
        String name,
        String type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );
    
    // Método para contar resultados - Versión 1: Usando nombre derivado
    Mono<Long> countByNameContainingIgnoreCaseAndTypeAndUploadedAtBetween(
        String name,
        String type,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
    
    // Método para contar resultados - Versión 2: Usando @Query (alternativa)
    @Query(value = """
        {
            $and: [
                {name: {$regex: ?0, $options: 'i'}},
                {?1 == null || type: ?1},
                {?2 == null || uploadedAt: {$gte: ?2}},
                {?3 == null || uploadedAt: {$lte: ?3}}
            ]
        }
        """, count = true)
    Mono<Long> countByFilters(
        String name,
        String type,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}