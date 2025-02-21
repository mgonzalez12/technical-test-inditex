package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.domain.model.repository.SpringDataAssetRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


@ExtendWith(MockitoExtension.class)
class AssetPersistenceAdapterTest {

    public static final  String ASSET_1 =  "asset-1";
    public static final  String IMAGE_PNG = "image.png";
    public static final  String CONTENT_TYPE ="image/png";
    public static final  String NON_EXISTENT ="non-existent";
    public static final  String UPLOAD_DATE = "uploadDate";
    public static final  String PENDING = "PENDING";
    public static final  String IMAGE ="image";
    public static final  String SORT_DIRECTION_ASC = "ASC";
    public static final  String SORT_DIRECTION_DESC = "DESC";
    @Mock
    private SpringDataAssetRepository repository;

    @InjectMocks
    private AssetPersistenceAdapter adapter;

    private AssetEntity testAsset;

    @BeforeEach
    void setUp() {
        // given: se crea un asset de prueba común para todos los tests
        testAsset = AssetEntity.builder()
                .id(ASSET_1)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE)
                .size(1024L)
                .uploadDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .status(PENDING)
                .build();
    }

    @Test
    void Give_ValidAsset_WhenSaveAssetIsCalled_ShouldReturnSavedAsset() {
        // given
        when(repository.save(any(AssetEntity.class))).thenReturn(testAsset);

        // when
        AssetEntity savedAsset = adapter.saveAsset(testAsset);

        // then
        verify(repository, times(1)).save(testAsset);
        assertNotNull(savedAsset);
        assertEquals(ASSET_1, savedAsset.getId());
        assertEquals(IMAGE_PNG, savedAsset.getFilename());
        assertEquals(CONTENT_TYPE, savedAsset.getContentType());
        assertEquals(1024L, savedAsset.getSize());
    }

    @Test
    void Give_ExistingAssetId_WhenFindByIdIsCalled_ShouldReturnAsset() {
        // given
        when(repository.findById(ASSET_1)).thenReturn(Optional.of(testAsset));

        // when
        AssetEntity foundAsset = adapter.findById(ASSET_1);

        // then
        verify(repository, times(1)).findById(ASSET_1);
        assertNotNull(foundAsset);
        assertEquals(ASSET_1, foundAsset.getId());
    }

    @Test
    void Give_NonExistingAssetId_WhenFindByIdIsCalled_ShouldReturnNull() {
        // given
        when(repository.findById(NON_EXISTENT)).thenReturn(Optional.empty());

        // when
        AssetEntity foundAsset = adapter.findById(NON_EXISTENT);

        // then
        verify(repository, times(1)).findById(NON_EXISTENT);
        assertNull(foundAsset);
    }

    @Test
    void Give_FilterParameters_WhenSearchAssetsIsCalled_WithAscendingSort_ShouldReturnListOfAssets() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        List<AssetEntity> expectedAssets = Collections.singletonList(testAsset);
        when(repository.findAll(any(Specification.class), eq(Sort.by(UPLOAD_DATE).ascending())))
                .thenReturn(expectedAssets);

        // when
        List<AssetEntity> result = adapter.searchAssets(startDate, endDate, IMAGE, CONTENT_TYPE, SORT_DIRECTION_ASC);

        // then
        verify(repository, times(1)).findAll(any(Specification.class), eq(Sort.by(UPLOAD_DATE).ascending()));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(ASSET_1, result.getFirst().getId());
    }

    @Test
    void Give_FilterParameters_WhenSearchAssetsIsCalled_WithDescendingSort_ShouldReturnListOfAssets() {
        // given
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);
        String filename = "";
        String filetype = "";
        List<AssetEntity> expectedAssets = Collections.singletonList(testAsset);
        when(repository.findAll(any(Specification.class), eq(Sort.by(UPLOAD_DATE).descending())))
                .thenReturn(expectedAssets);

        // when
        List<AssetEntity> result = adapter.searchAssets(startDate, endDate, filename, filetype, SORT_DIRECTION_DESC);

        // then
        verify(repository, times(1)).findAll(any(Specification.class), eq(Sort.by(UPLOAD_DATE).descending()));
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ASSET_1, result.getFirst().getId());
    }
}
