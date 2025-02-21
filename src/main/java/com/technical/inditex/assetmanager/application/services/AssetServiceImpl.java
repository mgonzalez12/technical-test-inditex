package com.technical.inditex.assetmanager.application.services;


import com.technical.inditex.assetmanager.application.usecases.AssetService;
import com.technical.inditex.assetmanager.domain.port.AssetPersistencePort;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.application.mapper.AssetEntityMapper;
import com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer.AssetUploadKafkaProducer;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.Asset;
import org.openapitools.model.AssetFileUploadRequest;
import org.openapitools.model.AssetFileUploadResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AssetServiceImpl implements AssetService {

    private final AssetPersistencePort assetPersistencePort;
    private final AssetUploadKafkaProducer kafkaProducer;
    private final AssetEntityMapper assetEntityMapper;
    // Executor que usa virtual threads
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public AssetServiceImpl(AssetPersistencePort assetPersistencePort, AssetUploadKafkaProducer kafkaProducer, AssetEntityMapper assetEntityMapper) {
        this.assetPersistencePort = assetPersistencePort;
        this.kafkaProducer = kafkaProducer;
        this.assetEntityMapper = assetEntityMapper;
    }

    @Override
    public AssetFileUploadResponse uploadAsset(AssetFileUploadRequest request) {
        // Para calcular el tamaño (si es null, 0)
        long size = request.getEncodedFile() != null
                ? request.getEncodedFile().length
                : 0L;

        // Construye la entidad
        AssetEntity assetEntity = AssetEntity.builder()
                .filename(request.getFilename())
                .contentType(request.getContentType())
                .size(size)
                .status("PENDING")
                .build();
        assetEntity = assetPersistencePort.saveAsset(assetEntity);

        // Envía a Kafka (si tu método de Kafka espera un String, debes convertir el byte[] a String)
        AssetEntity finalAssetEntity = assetEntity;
        virtualThreadExecutor.submit(() -> {
            try {
                // Si tu método de Kafka espera un String, conviértelo (por ejemplo, a Base64):
                String fileAsString = Base64.getEncoder().encodeToString(request.getEncodedFile());
                kafkaProducer.sendAssetUploadMessage(finalAssetEntity.getId(), fileAsString);

            } catch (Exception e) {
                log.error("Error enviando mensaje a Kafka para el asset {}: {}", finalAssetEntity.getId(), e.getMessage());
                finalAssetEntity.setStatus("FAILED");
                assetPersistencePort.saveAsset(finalAssetEntity);

            }
        });
        return new AssetFileUploadResponse().id(assetEntity.getId());

    }

    @Override
    @Retry(name = "assetSearch", fallbackMethod = "fallbackSearch")
    public List<Asset> searchAssets(String uploadDateStart, String uploadDateEnd, String filename, String filetype, String sortDirection) {
        try {
            LocalDateTime start = uploadDateStart != null ? LocalDate.parse(uploadDateStart, formatter).atStartOfDay() : null;
            LocalDateTime end = uploadDateEnd != null ? LocalDate.parse(uploadDateEnd, formatter).atStartOfDay() : null;
            List<AssetEntity> entities = assetPersistencePort.searchAssets(start, end, filename, filetype, sortDirection);
            return entities.stream()
                    .map(assetEntityMapper::toApiModel)
                    .collect(Collectors.toList());
        } catch (DateTimeParseException ex) {
            throw new RuntimeException("Error parseando fechas: " + ex.getMessage(), ex);
        }
    }

    // Fallback: devuelve una lista vacía y registra el error.
    public List<Asset> fallbackSearch(String uploadDateStart, String uploadDateEnd, String filename, String filetype, String sortDirection, Throwable t) {
        log.error("Fallback search activado: {}", t.getMessage());
        return Collections.emptyList();
    }

}
