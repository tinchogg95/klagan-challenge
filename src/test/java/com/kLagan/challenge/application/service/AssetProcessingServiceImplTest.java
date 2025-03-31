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

    private static final String ASSET_ID = "test-asset-123";
    private static final String PROCESSED_STATUS = "PROCESSED";
    private static final String FAILED_STATUS_PREFIX = "FAILED: ";
    private static final String ERROR_MESSAGE_SIZE_EXCEED = "Error en subida a almacenamiento: Tamaño de archivo excede el límite permitido";
    private static final String STORAGE_URL_PREFIX = "https://storage.example.com/files/";
    private static final String ERROR_MSJ ="Simulated error";
    private static final byte[] SMALL_FILE_CONTENT = new byte[1000];
    private static final byte[] LARGE_FILE_CONTENT = new byte[11_000_000];

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetProcessingServiceImpl assetProcessingService;

    @Test
    void processUploadedAsset_ShouldUpdateStatusToProcessed_WhenUploadSucceeds() {
        // setup for this void
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());
        when(assetRepository.updateUrl(anyString(), anyString())).thenReturn(Mono.empty());

        assetProcessingService.processUploadedAsset(ASSET_ID, SMALL_FILE_CONTENT);

        // test processed status
        verify(assetRepository, timeout(1000)).updateStatus(eq(ASSET_ID), eq(PROCESSED_STATUS));
        // test url returned
        verify(assetRepository, timeout(1000)).updateUrl(eq(ASSET_ID), contains(STORAGE_URL_PREFIX));
    }

    @Test
    void processUploadedAsset_ShouldUpdateStatusToFailed_WhenUploadFails() {
        // setup for this void
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());

        assetProcessingService.processUploadedAsset(ASSET_ID, LARGE_FILE_CONTENT);

        // test failed upload of file because of file size
        verify(assetRepository, timeout(1000)).updateStatus(
            eq(ASSET_ID), 
            eq(FAILED_STATUS_PREFIX + ERROR_MESSAGE_SIZE_EXCEED)
        );
        // test empty url returned
        verify(assetRepository, never()).updateUrl(anyString(), anyString());
    }

    @Test
    void markAsFailed_ShouldUpdateStatusWithErrorMessage() {
        when(assetRepository.updateStatus(anyString(), anyString())).thenReturn(Mono.empty());

        // mark as failed test
        assetProcessingService.markAsFailed(ASSET_ID, ERROR_MSJ);

        verify(assetRepository, timeout(1000))
            .updateStatus(eq(ASSET_ID), eq(FAILED_STATUS_PREFIX + ERROR_MSJ));
    }
}
