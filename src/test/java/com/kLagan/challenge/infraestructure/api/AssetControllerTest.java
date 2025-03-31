package com.kLagan.challenge.infraestructure.api;

import com.kLagan.challenge.application.service.AssetService;
import com.kLagan.challenge.domain.model.Asset;
import com.kLagan.challenge.infraestructure.api.dto.AssetFileUploadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(AssetController.class) // Habilita contexto WebFlux y auto-configura WebTestClient
public class AssetControllerTest {

    // Constants for URLs, paths, and mock data
    private static final String BASE_URL = "/api/mgmt/1/assets";
    private static final String UPLOAD_ACTION_URL = BASE_URL + "/actions/upload";
    private static final String MOCK_ASSET_ID = "test-123";
    private static final String FILENAME_KEY = "filename";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_MESSAGE_KEY = "errorMessage";
    private static final String MOCK_FILENAME = "test.pdf";
    private static final String MOCK_STATUS = "PROCESSED";
    private static final String MOCK_JSON_BODY = "{\"filename\":\"test.pdf\"}";
    private static final String MOCK_EMPTY_JSON_BODY = "{}";
    private static final String ERROR_MESSAGE = "Error de prueba";

    @Autowired
    private WebTestClient webTestClient; // Inyectado automáticamente

    @MockBean
    private AssetService assetService; // Mock para el servicio

    @Test
    void uploadAssetFile_ShouldReturnAccepted() {
        // Setup mock
        when(assetService.handleUpload(any(AssetFileUploadRequest.class)))
            .thenReturn(Mono.just(MOCK_ASSET_ID));
    
        // HTTP request and validations
        webTestClient.post()
            .uri(UPLOAD_ACTION_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(MOCK_JSON_BODY) 
            .exchange()
            .expectStatus().isAccepted()
            .expectBody()
            .jsonPath("$.id").isEqualTo(MOCK_ASSET_ID); 
    }

    @Test
    void getAssetsByFilter_ShouldReturnOk() {
        when(assetService.searchAssets(any(), any(), any(), any(), any(), any()))
            .thenReturn(Flux.empty());

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(BASE_URL)
                .queryParam(FILENAME_KEY, MOCK_FILENAME)
                .queryParam(STATUS_KEY, MOCK_STATUS)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Asset.class); // test returned list
    }

    @Test
    void uploadAssetFile_ShouldReturnErrorWhenServiceFails() {
        when(assetService.handleUpload(any()))
            .thenReturn(Mono.error(new RuntimeException(ERROR_MESSAGE)));

        webTestClient.post()
            .uri(UPLOAD_ACTION_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(MOCK_EMPTY_JSON_BODY)
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.errorMessage").exists();
    }
}
