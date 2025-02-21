package com.technical.inditex.assetmanager.application.mapper;


import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import org.mapstruct.Mapper;
import org.openapitools.model.Asset;


@Mapper(componentModel = "spring")
public interface AssetEntityMapper {
    Asset toApiModel(AssetEntity assetEntity);
}