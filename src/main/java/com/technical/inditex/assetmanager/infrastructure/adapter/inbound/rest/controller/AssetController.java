package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.controller;

import com.technical.inditex.assetmanager.application.usecases.AssetService;
import org.openapitools.api.ApiApi;
import org.openapitools.model.Asset;
import org.openapitools.model.AssetFileUploadRequest;
import org.openapitools.model.AssetFileUploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AssetController implements ApiApi {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Override
    public ResponseEntity<AssetFileUploadResponse> uploadAssetFile(AssetFileUploadRequest assetFileUploadRequest) {
            AssetFileUploadResponse response = assetService.uploadAsset(assetFileUploadRequest);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Override
    public ResponseEntity<List<Asset>> getAssetsByFilter(String uploadDateStart, String uploadDateEnd, String filename, String filetype, String sortDirection) {
        List<Asset> assets = assetService.searchAssets(uploadDateStart, uploadDateEnd, filename, filetype, sortDirection);
        return ResponseEntity.ok(assets);
    }
}

