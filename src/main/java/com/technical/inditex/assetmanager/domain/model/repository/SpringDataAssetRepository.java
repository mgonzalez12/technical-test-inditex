package com.technical.inditex.assetmanager.domain.model.repository;

import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SpringDataAssetRepository extends JpaRepository<AssetEntity, String>, JpaSpecificationExecutor<AssetEntity> {
}