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

    private static final String BASE_URL = "/api/mgmt/1/assets";

    @Autowired
    private WebTestClient webTestClient; // Inyectado automáticamente

    @MockBean
    private AssetService assetService; // Mock para el servicio

    @Test
    void uploadAssetFile_ShouldReturnAccepted() {
        //setup mock
        String mockAssetId = "test-123";
        when(assetService.handleUpload(any(AssetFileUploadRequest.class)))
            .thenReturn(Mono.just(mockAssetId));
    
        //http request and validations
        webTestClient.post()
            .uri(BASE_URL + "/actions/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"filename\":\"test.pdf\"}") 
            .exchange()
            .expectStatus().isAccepted()
            .expectBody()
            .jsonPath("$.id").isEqualTo(mockAssetId); 
    }

    @Test
    void getAssetsByFilter_ShouldReturnOk() {
        when(assetService.searchAssets(any(), any(), any(), any(), any(), any()))
            .thenReturn(Flux.empty());

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(BASE_URL)
                .queryParam("filename", "test.pdf")
                .queryParam("status", "PROCESSED")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Asset.class); // test returned list
    }

    @Test
    void uploadAssetFile_ShouldReturnErrorWhenServiceFails() {
        when(assetService.handleUpload(any()))
            .thenReturn(Mono.error(new RuntimeException("Error de prueba")));

        webTestClient.post()
            .uri(BASE_URL + "/actions/upload")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().is5xxServerError()
            .expectBody()
            .jsonPath("$.errorMessage").exists();
    }
}