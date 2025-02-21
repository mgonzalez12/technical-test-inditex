package com.technical.inditex.assetmanager.domain.port;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.domain.model.repository.SpringDataAssetRepository;
import com.technical.inditex.assetmanager.infrastructure.adapter.outbound.persistence.AssetPersistenceAdapter;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class AssetPersistenceAdapterTest {

    public static final  String STATUS_COMPLETED = "COMPLETED";
    public static final  String STATUS_PENDING = "PENDING";
    public static final  String IMAGE_PNG = "test.png";
    public static final  String CONTENT_TYPE_PNG ="image/png";
    public static final  String ID_ASSET = "asset-123";
    public static final  String NON_EXISTENT_ID = "non-existent-id";
    public static final String FILENAME = "test";
    public static final String SORT_DIRECTION = "ASC";

    @Mock
    private SpringDataAssetRepository repository;

    @InjectMocks
    private AssetPersistenceAdapter adapter;

    @Test
    void Give_ValidAssetEntity_WhenSaveAssetCalled_ShouldReturnSavedEntity() {
        // given: Se crea una entidad AssetEntity con valores de ejemplo.
        AssetEntity asset = AssetEntity.builder()
                .id(ID_ASSET)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE_PNG)
                .size(1234L)
                .status(STATUS_PENDING)
                .uploadDate(LocalDateTime.now())
                .build();
        when(repository.save(asset)).thenReturn(asset);

        // when: Se invoca el método saveAsset.
        AssetEntity result = adapter.saveAsset(asset);

        // then: Se verifica que la entidad se haya guardado y se pueda recuperar.
        assertNotNull(result);
        assertEquals(ID_ASSET, result.getId());
        verify(repository, times(1)).save(asset);
    }

    @Test
    void Give_ExistingAssetId_WhenFindByIdCalled_ShouldReturnAssetEntity() {
        // given: Se configura el repositorio para retornar una entidad para un id existente.
        AssetEntity asset = AssetEntity.builder()
                .id(ID_ASSET)
                .filename(IMAGE_PNG)
                .build();
        when(repository.findById(ID_ASSET)).thenReturn(Optional.of(asset));

        // when: Se invoca findById con el id existente.
        AssetEntity result = adapter.findById(ID_ASSET);

        // then: Se verifica que se retorna la entidad y que tiene el id esperado.
        assertNotNull(result);
        assertEquals(ID_ASSET, result.getId());
        verify(repository, times(1)).findById(ID_ASSET);
    }

    @Test
    void Give_NonExistingAssetId_WhenFindByIdCalled_ShouldReturnNull() {
        // given: Se configura el repositorio para retornar Optional.empty() cuando no se encuentre la entidad.
        when(repository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        // when: Se invoca findById con un id que no existe.
        AssetEntity result = adapter.findById(NON_EXISTENT_ID);

        // then: Se verifica que se retorne null.
        assertNull(result);
        verify(repository, times(1)).findById(NON_EXISTENT_ID);
    }

    @Test
    void Give_ValidSearchParameters_WhenSearchAssetsCalled_ShouldReturnAssetList() {
        // given: Definimos parámetros válidos para la búsqueda.
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        // given: Se simula que el repositorio retorna una lista con una entidad.
        AssetEntity asset = AssetEntity.builder()
                .id(ID_ASSET)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE_PNG)
                .size(2048L)
                .status(STATUS_COMPLETED)
                .uploadDate(LocalDateTime.now())
                .build();
        List<AssetEntity> entityList = Collections.singletonList(asset);
        // Se realiza el cast para evitar ambigüedades en el método findAll
        when(repository.findAll((Specification<AssetEntity>) any(), (Sort) any())).thenReturn(entityList);

        // when: Se invoca searchAssets con los parámetros dados.
        List<AssetEntity> result = adapter.searchAssets(start, end, FILENAME, CONTENT_TYPE_PNG, SORT_DIRECTION);

        // then: Se verifica que se retorne una lista no vacía y que el primer elemento tenga el id esperado.
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ID_ASSET, result.get(0).getId());
        verify(repository, times(1)).findAll((Specification<AssetEntity>) any(), (Sort) any());
    }
}
