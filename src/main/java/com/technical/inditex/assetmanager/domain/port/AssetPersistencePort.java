package com.technical.inditex.assetmanager.domain.port;

import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AssetPersistencePort {
    AssetEntity saveAsset(AssetEntity assetEntity);
    AssetEntity findById(String assetId);
    List<AssetEntity> searchAssets(LocalDateTime uploadDateStart, LocalDateTime uploadDateEnd, String filename, String filetype, String sortDirection);
}