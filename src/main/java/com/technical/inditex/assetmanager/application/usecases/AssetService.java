package com.technical.inditex.assetmanager.application.usecases;

import org.openapitools.model.Asset;
import org.openapitools.model.AssetFileUploadRequest;
import org.openapitools.model.AssetFileUploadResponse;

import java.util.List;

public interface AssetService {
    AssetFileUploadResponse uploadAsset(AssetFileUploadRequest request);
    List<Asset> searchAssets(String uploadDateStart, String uploadDateEnd, String filename, String filetype, String sortDirection);
}
