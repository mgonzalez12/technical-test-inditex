package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.consumer;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.domain.port.AssetPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetUploadKafkaConsumerTest {

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @InjectMocks
    private AssetUploadKafkaConsumer consumer; // Clase que estamos testeando

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Si es necesario, inicializa variables, mocks, etc.
    }

    @Test
    void consume_WhenMessageIsValidAndAssetExists_ShouldUpdateAsset() throws Exception {
        // GIVEN
        String assetId = "123";
        String jsonMessage = "{\"assetId\":\"" + assetId + "\",\"encodedFile\":\"someBase64Value\"}";

        // Creamos un asset de ejemplo en estado PENDING
        AssetEntity existingAsset = new AssetEntity();
        existingAsset.setId(assetId);
        existingAsset.setFilename("filename.png");
        existingAsset.setStatus("PENDING");

        // Mockeamos la respuesta de findById
        when(assetPersistencePort.findById(assetId)).thenReturn(existingAsset);

        // WHEN
        consumer.consume(jsonMessage);

        // THEN
        // Verificamos que se llame a findById con el assetId esperado
        verify(assetPersistencePort).findById(assetId);

        // Verificamos que se llame a saveAsset con la entidad actualizada
        verify(assetPersistencePort).saveAsset(argThat(asset ->
                asset.getId().equals(assetId) &&
                        "COMPLETED".equals(asset.getStatus()) &&
                        "http://assets.example.com/filename.png".equals(asset.getUrl())
        ));
    }

    @Test
    void consume_WhenMessageIsValidButAssetNotFound_ShouldDoNothing() throws Exception {
        // GIVEN
        String assetId = "456";
        String jsonMessage = "{\"assetId\":\"" + assetId + "\",\"encodedFile\":\"someBase64Value\"}";

        // Simulamos que no se encuentra el asset
        when(assetPersistencePort.findById(assetId)).thenReturn(null);

        // WHEN
        consumer.consume(jsonMessage);

        // THEN
        // Se llama a findById, pero no se llama a saveAsset
        verify(assetPersistencePort).findById(assetId);
        verify(assetPersistencePort, never()).saveAsset(any());
    }

    @Test
    void consume_WhenMessageIsInvalid_ShouldCatchExceptionAndNotThrow() {
        // GIVEN
        // Mensaje inválido (no es JSON o le falta assetId)
        String invalidMessage = "invalid-json";

        // WHEN - no debería lanzar excepción, sino atraparla
        consumer.consume(invalidMessage);

        // THEN
        // Se verifica que no se llama a findById ni a saveAsset
        verify(assetPersistencePort, never()).findById(any());
        verify(assetPersistencePort, never()).saveAsset(any());
    }
}
