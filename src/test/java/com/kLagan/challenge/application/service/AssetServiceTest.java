package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetPublisher;
import com.kLagan.challenge.application.port.AssetRepository;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    private static final String TEST_FILENAME = "testFile.txt";
    private static final String TEST_CONTENT_TYPE = "text/plain";
    private static final String TEST_BASE64_ENCODED_FILE = "data:text/plain;base64,SGVsbG8gd29ybGQ=";
    private static final String TEST_INVALID_BASE64 = "invalid-base64";
    private static final String TEST_ID = "test-id";
    private static final String UPLOADED_STATUS = "UPLOADED";
    private static final int FILE_SIZE = 15; // size of the decoded content
    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB

    @Mock
    private AssetRepository repository;

    @Mock
    private AssetPublisher publisher;

    @InjectMocks
    private AssetService assetService;

    private AssetFileUploadRequest validRequest;

    @BeforeEach
    public void setup() {
        validRequest = new AssetFileUploadRequest();
        validRequest.setFilename(TEST_FILENAME);
        validRequest.setContentType(TEST_CONTENT_TYPE);
        validRequest.setEncodedFile(TEST_BASE64_ENCODED_FILE);
    }

    @Test
    public void testHandleUpload_successful() {
        Asset asset = new Asset();
        asset.setId(TEST_ID);
        asset.setFilename(validRequest.getFilename());
        asset.setContentType(validRequest.getContentType());
        asset.setSize(FILE_SIZE);
        asset.setStatus(UPLOADED_STATUS);

        when(repository.save(any(Asset.class))).thenReturn(Mono.just(asset));
        doNothing().when(publisher).publishAssetAsync(anyString(), any(byte[].class));

        Mono<String> result = assetService.handleUpload(validRequest);

        StepVerifier.create(result)
                .assertNext(id -> {
                    assertNotNull(id);
                    assertTrue(id.matches("^[0-9a-fA-F-]{36}$"));
                })
                .verifyComplete();
        verify(repository, times(1)).save(any(Asset.class));
        verify(publisher, times(1)).publishAssetAsync(anyString(), any(byte[].class));
    }

    @Test
    public void testHandleUpload_invalidFileSize() {
        validRequest.setEncodedFile("data:text/plain;base64," + "A".repeat(MAX_FILE_SIZE + 1)); // 20MB + 1

        Mono<String> result = assetService.handleUpload(validRequest);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void testHandleUpload_missingFileName() {
        validRequest.setFilename(null);  // Invalid request

        Mono<String> result = assetService.handleUpload(validRequest);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void testHandleUpload_invalidBase64() {
        validRequest.setEncodedFile(TEST_INVALID_BASE64);

        Mono<String> result = assetService.handleUpload(validRequest);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
