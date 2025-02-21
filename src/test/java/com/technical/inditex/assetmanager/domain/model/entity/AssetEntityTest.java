package com.technical.inditex.assetmanager.domain.model.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AssetEntityTest {

    public static final  String STATUS_COMPLETED = "COMPLETED";
    public static final  String STATUS_PENDING = "PENDING";
    public static final  String IMAGE_PNG = "test.png";
    public static final  String CONTENT_TYPE_PNG ="image/png";
    public static final  String IMAGE_JPG = "image.jpg";
    public static final  String CONTENT_TYPE_JPG ="image/jpg";
    public static final  String IMAGE_URL = "http://assets.example.com/image.png";
    public static final  String MESSAGE_ID_EXISTE = "El id no debe cambiar si ya existe";
    public static final  String MESSAGE_UPLOADATE = "El uploadDate debe ser asignado por prePersist";
    public static final  String MESSAGE_UPLOADATE_POSTERIOR = "El uploadDate debe ser posterior a la fecha antigua";
    public static final  String MESSAGE_ID_POR_PERSISTENCE = "El id debe ser asignado por prePersist si es nulo";
    public static final  String EXISTING_ID = "existing-id";
    public static final  String ID = "asset-456";
    public static final  Long SIZE = 4096L;

    @Test
    void Give_NullId_WhenPrePersistCalled_ShouldSetIdAndUploadDate() {
        // given: Se crea una entidad AssetEntity sin id ni uploadDate
        AssetEntity asset = AssetEntity.builder()
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE_PNG)
                .size(1024L)
                .status(STATUS_PENDING)
                .build();
        asset.setId(null);
        asset.setUploadDate(null);

        // when: Se invoca prePersist
        asset.prePersist();

        // then: Se espera que se asignen un id y una fecha de subida
        assertNotNull(asset.getId(), MESSAGE_ID_POR_PERSISTENCE);
        assertNotNull(asset.getUploadDate(), MESSAGE_UPLOADATE);
    }

    @Test
    void Give_ExistingId_WhenPrePersistCalled_ShouldNotChangeIdButUpdateUploadDate() {
        // given: Se crea una entidad AssetEntity con un id existente y una fecha antigua
        LocalDateTime oldDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        AssetEntity asset = AssetEntity.builder()
                .id(EXISTING_ID)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE_PNG)
                .size(2048L)
                .status(STATUS_PENDING)
                .build();
        asset.setUploadDate(oldDate);

        // when: Se invoca prePersist
        asset.prePersist();

        // then: El id debe mantenerse y la fecha de subida debe actualizarse (ser posterior a la antigua)
        assertEquals(EXISTING_ID, asset.getId(), MESSAGE_ID_EXISTE);
        assertNotNull(asset.getUploadDate(), MESSAGE_UPLOADATE);
        assertTrue(asset.getUploadDate().isAfter(oldDate), MESSAGE_UPLOADATE_POSTERIOR);
    }

    @Test
    void Give_Values_WhenUsingBuilder_ShouldReturnEntityWithThoseValues() {
        // given: Se crean valores para la entidad
        LocalDateTime now = LocalDateTime.now();
        String status = STATUS_COMPLETED;

        // when: Se crea la entidad usando el builder
        AssetEntity asset = AssetEntity.builder()
                .id(ID)
                .filename(IMAGE_JPG)
                .contentType(CONTENT_TYPE_JPG)
                .url(IMAGE_URL)
                .size(SIZE)
                .uploadDate(now)
                .status(status)
                .build();

        // then: Los getters deben retornar los valores asignados
        assertEquals(ID, asset.getId());
        assertEquals(IMAGE_JPG, asset.getFilename());
        assertEquals(CONTENT_TYPE_JPG, asset.getContentType());
        assertEquals(IMAGE_URL, asset.getUrl());
        assertEquals(SIZE, asset.getSize());
        assertEquals(now, asset.getUploadDate());
        assertEquals(status, asset.getStatus());
    }
}
