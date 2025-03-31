package com.kLagan.challenge.infraestructure.publisher;

import com.kLagan.challenge.domain.model.AssetUploadEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaAssetPublisherTest {

    @Mock
    private KafkaTemplate<String, AssetUploadEvent> kafkaTemplate;

    @InjectMocks
    private KafkaAssetPublisher kafkaAssetPublisher;

    private final String testAssetId = "test-asset-123";
    private final byte[] testFileContent = new byte[]{1, 2, 3};

    // succesfull case test
    @Test
    void publishAssetAsync_ShouldSendToKafka_WhenCalled() throws Exception {
        // setup
        SendResult<String, AssetUploadEvent> mockResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, AssetUploadEvent>> future = 
            CompletableFuture.completedFuture(mockResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AssetUploadEvent.class)))
            .thenReturn(future);

        kafkaAssetPublisher.publishAssetAsync(testAssetId, testFileContent);

        ArgumentCaptor<AssetUploadEvent> eventCaptor = 
            ArgumentCaptor.forClass(AssetUploadEvent.class);
        
        verify(kafkaTemplate, timeout(1000))
            .send(eq("asset-uploads"), eq(testAssetId), eventCaptor.capture());

        // Event validation
        AssetUploadEvent capturedEvent = eventCaptor.getValue();
        assertEquals(testAssetId, capturedEvent.getAssetId());
        assertArrayEquals(testFileContent, capturedEvent.getFileContent());
    }

    // failed case
    @Test
    void publishAssetAsync_ShouldHandleFailure_WhenKafkaFails() {
        // setup with exception
        CompletableFuture<SendResult<String, AssetUploadEvent>> failedFuture = 
            new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AssetUploadEvent.class)))
            .thenReturn(failedFuture);

        assertDoesNotThrow(() -> 
            kafkaAssetPublisher.publishAssetAsync(testAssetId, testFileContent)
        );
    }

    @Test
    void publishAssetAsyncWithFileLocation_ShouldThrowUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> 
            kafkaAssetPublisher.publishAssetAsync(testAssetId, "/path/to/file")
        );
    }
}