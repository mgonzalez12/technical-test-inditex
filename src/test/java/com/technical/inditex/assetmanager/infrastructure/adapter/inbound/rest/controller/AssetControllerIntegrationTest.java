package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.openapitools.model.AssetFileUploadRequest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AssetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Give_ValidUploadRequest_WhenUploadAssetFileCalled_ShouldReturnAcceptedResponseAndAssetId
     *
     * Given un request válido para subir un asset,
     * When se llama al endpoint POST /api/mgmt/1/assets/actions/upload,
     * Then se debe retornar un código 202 (Accepted) y en la respuesta un id no nulo.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void Give_ValidUploadRequest_WhenUploadAssetFileCalled_ShouldReturnAcceptedResponseAndAssetId() throws Exception {
        // given
        AssetFileUploadRequest request = new AssetFileUploadRequest();
        request.setFilename("image.png");
        // Se simula un archivo (convertido a byte[])
        request.setEncodedFile("sampleBase64".getBytes());
        request.setContentType("image/png");

        // when & then
        mockMvc.perform(post("/api/mgmt/1/assets/actions/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    /**
     * Give_ValidFilterParameters_WhenGetAssetsByFilterCalled_ShouldReturnAssetList
     *
     * Given parámetros válidos para filtrar assets,
     * When se llama al endpoint GET /api/mgmt/1/assets/,
     * Then se debe retornar un código 200 (OK) y el contenido debe ser un arreglo JSON (aunque puede estar vacío si no hay datos).
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void Give_ValidFilterParameters_WhenGetAssetsByFilterCalled_ShouldReturnAssetList() throws Exception {
        // given
        String uploadDateStart = "2025-01-01T00:00:00Z";
        String uploadDateEnd = "2025-12-31T23:59:59Z";
        String filename = "image";
        String filetype = "image/png";
        String sortDirection = "ASC";

        // when & then
        mockMvc.perform(get("/api/mgmt/1/assets/")
                        .param("uploadDateStart", uploadDateStart)
                        .param("uploadDateEnd", uploadDateEnd)
                        .param("filename", filename)
                        .param("filetype", filetype)
                        .param("sortDirection", sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
