package com.technical.inditex.assetmanager.domain.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5434/assetdb",
        "spring.datasource.username=user",
        "spring.datasource.password=password",
        "spring.jpa.hibernate.ddl-auto=none"
})
class SpringDataAssetRepositoryTest {

    public static final  String STATUS_COMPLETED = "COMPLETED";
    public static final  String STATUS_PENDING = "PENDING";
    public static final  String IMAGE_PNG = "test.png";
    public static final  String IMAGE_PNG_TWO = "example.png";
    public static final  String CONTENT_TYPE_PNG = "image/png";
    public static final  String CONTENT = "example";

    @Autowired
    private SpringDataAssetRepository repository;

    @Test
    void givenNewAsset_whenSaveCalled_shouldPersistAsset() {
        // given: Se crea una entidad AssetEntity con valores de ejemplo
        String id = UUID.randomUUID().toString();
        AssetEntity asset = AssetEntity.builder()
                .id(id)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE_PNG)
                .size(1234L)
                .status(STATUS_PENDING)
                .uploadDate(LocalDateTime.now())
                .build();

        // when: Se guarda la entidad en el repositorio
        AssetEntity savedAsset = repository.save(asset);

        // then: Se verifica que la entidad se haya guardado y se pueda recuperar
        Optional<AssetEntity> found = repository.findById(savedAsset.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFilename()).isEqualTo(IMAGE_PNG);
    }

    @Test
    void givenExistingAsset_whenFindAllWithSpecificationCalled_shouldReturnMatchingAsset() {
        // given: Se guarda una entidad con filename "example.png"
        String id = UUID.randomUUID().toString();
        AssetEntity asset = AssetEntity.builder()
                .id(id)
                .filename(IMAGE_PNG_TWO)
                .contentType(CONTENT_TYPE_PNG)
                .size(5678L)
                .status(STATUS_COMPLETED)
                .uploadDate(LocalDateTime.now())
                .build();
        repository.save(asset);

        // given: Se crea una especificación para buscar assets cuyo filename contenga "example"
        Specification<AssetEntity> spec = (root, query, cb) -> cb.like(root.get("filename"), "%example%");

        // when: Se consulta el repositorio usando la especificación
        List<AssetEntity> result = repository.findAll(spec);

        // then: Se debe obtener al menos una entidad y su filename debe contener "example"
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getFilename()).contains(CONTENT);
    }
}
