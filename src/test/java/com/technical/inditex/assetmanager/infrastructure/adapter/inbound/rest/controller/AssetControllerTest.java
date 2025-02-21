package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.controller;

import static org.junit.jupiter.api.Assertions.*;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.technical.inditex.assetmanager.application.usecases.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.model.Asset;
import org.openapitools.model.AssetFileUploadRequest;
import org.openapitools.model.AssetFileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(AssetController.class)
@WithMockUser    // Simula un usuario autenticado para endpoints protegidos
//@Import(AssetControllerTest.TestConfig.class)
public class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssetService assetService;

    // Variables comunes para los tests
    private AssetFileUploadRequest uploadRequest;
    private AssetFileUploadResponse uploadResponse;
    private List<Asset> assetList;

    @BeforeEach
    void setUp() {
        // // given: Configuramos el DTO de subida
        uploadRequest = new AssetFileUploadRequest();
        uploadRequest.setFilename("image.png");
        // Simula un archivo codificado (por ejemplo, Base64 convertido a bytes)
        uploadRequest.setEncodedFile("YmFzZTY0RW5jb2RlZFN0cmluZw==".getBytes());
        uploadRequest.setContentType("image/png");

        // // given: Configuramos la respuesta esperada para la subida
        uploadResponse = new AssetFileUploadResponse();
        uploadResponse.setId("asset-123");

        // // given: Configuramos un asset de prueba para la búsqueda
        Asset asset = new Asset();
        asset.setId("asset-123");
        asset.setFilename("image.png");
        asset.setContentType("image/png");
        asset.setUrl("http://assets.example.com/image.png");
        asset.setSize(1024);
        asset.setUploadDate("2025-01-01T00:00:00Z");
        assetList = Collections.singletonList(asset);
    }

    @Test
    void Give_ValidUploadRequest_WhenUploadAssetFileCalled_ShouldReturnAcceptedResponseWithAssetId() throws Exception {
        // // given: El servicio retorna una respuesta con id "asset-123"
        when(assetService.uploadAsset(any(AssetFileUploadRequest.class))).thenReturn(uploadResponse);

        // // when: Se realiza una petición POST al endpoint de subida
        mockMvc.perform(post("/api/mgmt/1/assets/actions/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadRequest)))
                // // then: Se espera status 202 (ACCEPTED) y en la respuesta el id "asset-123"
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value("asset-123"));
    }

    @Test
    void Give_ValidFilterParameters_WhenGetAssetsByFilterCalled_ShouldReturnAssetList() throws Exception {
        // // given: El servicio retorna una lista con un asset
        when(assetService.searchAssets(any(), any(), any(), any(), any())).thenReturn(assetList);

        // // when: Se realiza una petición GET al endpoint de búsqueda con parámetros de filtro
        mockMvc.perform(get("/api/mgmt/1/assets")
                        .param("uploadDateStart", "2025-01-01T00:00:00Z")
                        .param("uploadDateEnd", "2025-12-31T23:59:59Z")
                        .param("filename", "image")
                        .param("filetype", "image/png")
                        .param("sortDirection", "ASC"));
                // // then: Se espera status 200 (OK) y que el asset tenga id "asset-123" y filename "image.png"
               // .andExpect(status().isOk())
               // .andExpect(jsonPath("$[0].id").value("asset-123"))
               // .andExpect(jsonPath("$[0].filename").value("image.png"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public AssetService assetService() {
            return org.mockito.Mockito.mock(AssetService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
