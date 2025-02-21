package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.persistence;


import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.domain.port.AssetPersistencePort;
import com.technical.inditex.assetmanager.domain.model.repository.SpringDataAssetRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AssetPersistenceAdapter implements AssetPersistencePort {

    private final SpringDataAssetRepository repository;

    public AssetPersistenceAdapter(SpringDataAssetRepository repository) {
        this.repository = repository;
    }

    @Override
    public AssetEntity saveAsset(AssetEntity assetEntity) {
        return repository.save(assetEntity);
    }

    @Override
    public AssetEntity findById(String assetId) {
        return repository.findById(assetId).orElse(null);
    }

    @Override
    public List<AssetEntity> searchAssets(LocalDateTime uploadDateStart, LocalDateTime uploadDateEnd, String filename, String filetype, String sortDirection) {
        Specification<AssetEntity> spec = Specification.where(null);
        if (uploadDateStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("uploadDate"), uploadDateStart));
        }
        if (uploadDateEnd != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("uploadDate"), uploadDateEnd));
        }
        if (filename != null && !filename.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("filename"), "%" + filename + "%"));
        }
        if (filetype != null && !filetype.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("contentType"), filetype));
        }
        Sort sort = "DESC".equalsIgnoreCase(sortDirection)
                ? Sort.by("uploadDate").descending()
                : Sort.by("uploadDate").ascending();
        return repository.findAll(spec, sort);
    }
}

