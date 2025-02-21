package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technical.inditex.assetmanager.domain.port.AssetPersistencePort;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssetUploadKafkaConsumer {

    private final AssetPersistencePort assetPersistencePort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AssetUploadKafkaConsumer(AssetPersistencePort assetPersistencePort) {
        this.assetPersistencePort = assetPersistencePort;
    }

    @KafkaListener(topics = "asset-upload", groupId = "asset-group")
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String assetId = jsonNode.get("assetId").asText();
            log.info("Envio asset ID : {}", assetId);
            AssetEntity assetEntity = assetPersistencePort.findById(assetId);
            if (assetEntity != null) {
                assetEntity.setUrl("http://assets.example.com/" + assetEntity.getFilename());
                assetEntity.setStatus("COMPLETED");
                assetPersistencePort.saveAsset(assetEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
