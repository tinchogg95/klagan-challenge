package com.kLagan.challenge.infraestructure.consumer;

import com.kLagan.challenge.domain.model.AssetUploadEvent;
import com.kLagan.challenge.application.port.AssetProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetUploadConsumerTest {

    @Mock
    private AssetProcessingService processingService;

    @InjectMocks
    private AssetUploadConsumer assetUploadConsumer;

    private AssetUploadEvent validEvent;
    private AssetUploadEvent invalidEvent;

    @BeforeEach
    void setUp() {
        validEvent = new AssetUploadEvent("123", new byte[]{1, 2, 3});
        invalidEvent = new AssetUploadEvent("456", (byte[]) null);
    }

    @Test
    void consume_ShouldProcessValidAsset() {
        assetUploadConsumer.consume(validEvent);

        verify(processingService, times(1)).processUploadedAsset(validEvent.assetId(), validEvent.fileContent());
        verify(processingService, never()).markAsFailed(any(), any());
    }

    @Test
    void consume_ShouldMarkAsFailed_WhenFileContentIsNull() {
        assetUploadConsumer.consume(invalidEvent);

        verify(processingService, never()).processUploadedAsset(any(), any());
        verify(processingService, times(1)).markAsFailed("456", "Contenido del archivo es nulo");
    }

    @Test
    void consume_ShouldMarkAsFailed_OnException() {
        doThrow(new RuntimeException("Test Exception"))
                .when(processingService).processUploadedAsset(any(), any());

        assetUploadConsumer.consume(validEvent);

        verify(processingService, times(1)).markAsFailed("123", "Error interno: Test Exception");
    }
}
