package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer;

public interface AssetUploadKafkaProducer {
    void sendAssetUploadMessage(String assetId, String encodedFile);
}
