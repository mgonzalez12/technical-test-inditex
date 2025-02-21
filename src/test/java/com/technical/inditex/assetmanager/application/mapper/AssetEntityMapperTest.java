package com.technical.inditex.assetmanager.application.mapper;


import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.Asset;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AssetEntityMapperTest {

    public static final  String IMAGE_PNG = "image.png";
    public static final  String CONTENT_TYPE ="image/png";
    public static final  String ASSET = "asset-123";
    public static final  String IMAGE_URL = "http://assets.example.com/image.png";
    public static final  String DATE_START = "2025-01-01T00:00:00";

    // Se obtiene la instancia del mapper
    private final AssetEntityMapper mapper = Mappers.getMapper(AssetEntityMapper.class);

    @Test
    void Give_ValidAssetEntity_WhenToApiModelIsCalled_ShouldReturnMappedAsset() {
        // given: Se crea una entidad de asset con todos los campos definidos
        AssetEntity entity = AssetEntity.builder()
                .id(ASSET)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE)
                .url(IMAGE_URL)
                .size(1024L)
                .uploadDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        // when: Se llama al método toApiModel para mapear la entidad a DTO
        Asset asset = mapper.toApiModel(entity);

        // then: Se verifica que el objeto mapeado no sea nulo y que sus campos coincidan
        assertNotNull(asset);
        assertEquals(ASSET, asset.getId());
        assertEquals(IMAGE_PNG, asset.getFilename());
        assertEquals(CONTENT_TYPE, asset.getContentType());
        assertEquals(IMAGE_URL, asset.getUrl());
        assertEquals(1024, asset.getSize());
        // Se asume que el mapper convierte el LocalDateTime a un String ISO sin zona horaria
        assertEquals(DATE_START, asset.getUploadDate());
    }
}
