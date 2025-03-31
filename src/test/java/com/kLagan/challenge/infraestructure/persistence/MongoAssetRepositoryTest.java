package com.kLagan.challenge.infraestructure.persistence;

import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.application.port.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoAssetRepositoryTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    private MongoAssetRepository assetRepository;

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setId("123");
        asset.setFilename("test-file.png");
        asset.setContentType("image/png");
        asset.setUploadDate(LocalDateTime.now());
        asset.setStatus("ACTIVE");
    }

    @Test
    void save_ShouldReturnSavedAsset() {
        when(mongoTemplate.save(asset)).thenReturn(Mono.just(asset));

        Mono<Asset> result = assetRepository.save(asset);

        assertNotNull(result);
        assertEquals(asset.getId(), result.block().getId());
        verify(mongoTemplate, times(1)).save(asset);
    }

    @Test
    void search_ShouldReturnMatchingAssets() {
        when(mongoTemplate.find(any(Query.class), eq(Asset.class))).thenReturn(Flux.just(asset));
        Flux<Asset> result = assetRepository.search(LocalDateTime.now().minusDays(1), LocalDateTime.now(), "test", "image/png", "ACTIVE", "ASC");
        assertNotNull(result);
        assertEquals(1, result.collectList().block().size());
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(Asset.class));
    }

    @Test
    void findById_ShouldReturnAsset() {
        when(mongoTemplate.findById("123", Asset.class)).thenReturn(Mono.just(asset));
        Mono<Asset> result = assetRepository.findById("123");
        assertNotNull(result);
        assertEquals(asset.getId(), result.block().getId());
        verify(mongoTemplate, times(1)).findById("123", Asset.class);
    }

    @Test
    void updateStatus_ShouldUpdateStatusSuccessfully() {
        when(mongoTemplate.updateFirst(any(Query.class), any(), eq(Asset.class))).thenReturn(Mono.empty());
        Mono<Void> result = assetRepository.updateStatus("123", "INACTIVE");
        assertNotNull(result);
        result.block();
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(), eq(Asset.class));
    }

    @Test
    void updateUrl_ShouldUpdateUrlSuccessfully() {
        when(mongoTemplate.updateFirst(any(Query.class), any(), eq(Asset.class))).thenReturn(Mono.empty());
        Mono<Void> result = assetRepository.updateUrl("123", "http://new-url.com");
        assertNotNull(result);
        result.block();
        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(), eq(Asset.class));
    }
}
