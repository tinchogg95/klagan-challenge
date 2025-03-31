package com.kLagan.challenge.application.service;

import com.kLagan.challenge.application.port.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetProcessingServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetProcessingServiceImpl assetProcessingService;

    private final String assetId = "test-asset-123";
    private final byte[] smallFileContent = new byte[1000];
    private final byte[] largeFileContent = new byte[11_000_000];

    @Test
    void processUploadedAsset_ShouldUpdateStatusToProcessed_WhenUploadSucceeds() {
        // setup for this void
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());
        when(assetRepository.updateUrl(anyString(), anyString())).thenReturn(Mono.empty());

        assetProcessingService.processUploadedAsset(assetId, smallFileContent);
        // test processed status
        verify(assetRepository, timeout(1000)).updateStatus(eq(assetId), eq("PROCESSED"));
        // test url returned
        verify(assetRepository, timeout(1000)).updateUrl(eq(assetId), contains("https://storage.example.com/files/"));
    }

    @Test
    void processUploadedAsset_ShouldUpdateStatusToFailed_WhenUploadFails() {
        // setup for this void
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());

        assetProcessingService.processUploadedAsset(assetId, largeFileContent);
        //test failed upload of file because of file size
        verify(assetRepository, timeout(1000)).updateStatus(
            eq(assetId), 
            eq("FAILED: Error en subida a almacenamiento: Tamaño de archivo excede el límite permitido")
        );
        //test empty url returned
        verify(assetRepository, never()).updateUrl(anyString(), anyString());
    }

    @Test
    void markAsFailed_ShouldUpdateStatusWithErrorMessage() {
        String errorMessage = "Simulated error";
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());
        //mark as failed test
        assetProcessingService.markAsFailed(assetId, errorMessage);

        verify(assetRepository, timeout(1000))
            .updateStatus(eq(assetId), eq("FAILED: " + errorMessage));
    }
}