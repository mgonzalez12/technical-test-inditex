package com.technical.inditex.assetmanager.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity {

    @Id
    private String id;

    private String filename;

    @Column(name = "content_type")
    private String contentType;

    private String url;

    private Long size;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // Controla el estado del procesamiento: PENDING, COMPLETED, etc.
    private String status;


    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        uploadDate = LocalDateTime.now();
    }
}

