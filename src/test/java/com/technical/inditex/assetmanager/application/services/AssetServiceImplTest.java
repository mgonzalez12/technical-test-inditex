package com.technical.inditex.assetmanager.application.services;

import com.technical.inditex.assetmanager.application.mapper.AssetEntityMapper;
import com.technical.inditex.assetmanager.domain.model.entity.AssetEntity;
import com.technical.inditex.assetmanager.domain.port.AssetPersistencePort;
import com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer.AssetUploadKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.Asset;
import org.openapitools.model.AssetFileUploadRequest;
import org.openapitools.model.AssetFileUploadResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Collections;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.util.concurrent.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AssetServiceImplTest {

    public static final  String IMAGE_PNG = "image.png";
    public static final  String CONTENT_TYPE ="image/png";
    public static final  String ASSET = "asset-123";
    public static final  String UPLOAD_DATE_START = "2025-01-01T00:00:00Z";
    public static final  String UPLOAD_DATE_END = "2025-12-31T00:00:00Z";
    public static final  String FILE_NAME = "image";
    public static final  String SORT_DIRECTION = "ASC";
    public static final  String VIRTUAL_THREAD_EXECUTOR = "virtualThreadExecutor";
    public static final  String ERROR_PROCESANDO_FECHA = "Error parseando fechas: Invalid date";
    public static final  String INVALID_UPLOAD_DATE_START = "2025-02-30T00:00:00Z";
    public static final  String IMAGE_URL = "http://assets.example.com/image.png";
    public static final  String STATUS_COMPLETED = "COMPLETED";
    public static final  String STATUS_PENDING = "PENDING";
    public static final  String TEST_FILE_CONTENT = "testFileContent";

    @Mock
    private AssetPersistencePort assetPersistencePort;

    @Mock
    private AssetUploadKafkaProducer kafkaProducer;

    @Mock
    private AssetEntityMapper assetEntityMapper;

    @InjectMocks
    private AssetServiceImpl assetService;

    // Valores comunes para los tests
    private AssetFileUploadRequest uploadRequest;
    private AssetEntity assetEntity;
    private AssetFileUploadResponse uploadResponse;

    // Usamos el formateador ISO
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * ExecutorService síncrono que ejecuta la tarea inmediatamente.
     */
    private ExecutorService directExecutor() {
        return new ExecutorService() {
            @Override public void execute(Runnable command) {
                command.run();
            }
            @Override public void shutdown() { }
            @Override public List<Runnable> shutdownNow() { return Collections.emptyList(); }
            @Override public boolean isShutdown() { return false; }
            @Override public boolean isTerminated() { return false; }
            @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
            @Override public <T> Future<T> submit(Callable<T> task) {
                try { return CompletableFuture.completedFuture(task.call()); } catch (Exception e) { throw new RuntimeException(e); }
            }
            @Override public <T> Future<T> submit(Runnable task, T result) {
                task.run();
                return CompletableFuture.completedFuture(result);
            }
            @Override public Future<?> submit(Runnable task) {
                task.run();
                return CompletableFuture.completedFuture(null);
            }
            @Override public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) { throw new UnsupportedOperationException(); }
            @Override public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) { throw new UnsupportedOperationException(); }
            @Override public <T> T invokeAny(Collection<? extends Callable<T>> tasks) { throw new UnsupportedOperationException(); }
            @Override public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) { throw new UnsupportedOperationException(); }
        };
    }

    @BeforeEach
    void setUp() throws Exception {
        // given: Configuramos el request de subida
        uploadRequest = new AssetFileUploadRequest();
        uploadRequest.setFilename(IMAGE_PNG);
        // Simulamos un archivo: "testFileContent" convertido a bytes
        uploadRequest.setEncodedFile(TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8));
        uploadRequest.setContentType(CONTENT_TYPE);

        // given: Configuramos la entidad del asset en estado PENDING
        assetEntity = AssetEntity.builder()
                .id(ASSET)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE)
                .size((long) uploadRequest.getEncodedFile().length)
                .status(STATUS_PENDING)
                .build();

        // given: Configuramos la respuesta esperada para la subida
        uploadResponse = new AssetFileUploadResponse();
        uploadResponse.setId(ASSET);

        // Lenient: simulamos que al guardar el asset se retorna la entidad configurada
        lenient().when(assetPersistencePort.saveAsset(any(AssetEntity.class))).thenReturn(assetEntity);

        // Reemplazamos el executor asíncrono por uno síncrono para que las tareas se ejecuten inmediatamente.
        ReflectionTestUtils.setField(assetService, VIRTUAL_THREAD_EXECUTOR, directExecutor());
    }

    @Test
    void Give_ValidUploadRequest_WhenUploadAssetCalled_ShouldReturnResponseAndSendKafkaMessage() {
        // given: Calculamos el Base64 esperado para el encodedFile
        String expectedBase64 = java.util.Base64.getEncoder().encodeToString(uploadRequest.getEncodedFile());
        // given: Simulamos que el productor Kafka envía el mensaje sin error
        doNothing().when(kafkaProducer).sendAssetUploadMessage(ASSET, expectedBase64);

        // when: Se invoca el método uploadAsset
        AssetFileUploadResponse response = assetService.uploadAsset(uploadRequest);

        // then: La respuesta debe contener el id "asset-123"
        assertNotNull(response);
        assertEquals(ASSET, response.getId());
        // then: Se verifica que se haya llamado al método del kafkaProducer con los parámetros correctos
        verify(kafkaProducer, times(1)).sendAssetUploadMessage(ASSET, expectedBase64);
    }

    @Test
    void Give_ValidSearchParameters_WhenSearchAssetsCalled_ShouldReturnMappedAssets() {
        // given: Simulamos que el persistence port retorna una lista con una entidad
        AssetEntity entity = AssetEntity.builder()
                .id(ASSET)
                .filename(IMAGE_PNG)
                .contentType(CONTENT_TYPE)
                .size(1024L)
                .status(STATUS_COMPLETED)
                .build();
        List<AssetEntity> entityList = Collections.singletonList(entity);
        when(assetPersistencePort.searchAssets(any(), any(), any(), any(), any())).thenReturn(entityList);

        // given: Simulamos que el mapper convierte la entidad en un DTO Asset
        Asset assetDto = new Asset();
        assetDto.setId(ASSET);
        assetDto.setFilename(IMAGE_PNG);
        assetDto.setContentType(CONTENT_TYPE);
        assetDto.setUrl(IMAGE_URL);
        assetDto.setSize(1024);
        assetDto.setUploadDate(UPLOAD_DATE_START);
        when(assetEntityMapper.toApiModel(entity)).thenReturn(assetDto);

        // when: Se invoca searchAssets con los parámetros dados
        List<Asset> result = assetService.searchAssets(UPLOAD_DATE_START, UPLOAD_DATE_END, FILE_NAME, CONTENT_TYPE, SORT_DIRECTION);

        // then: Se debe retornar una lista con el asset mapeado
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(ASSET, result.get(0).getId());
        assertEquals(IMAGE_PNG, result.getFirst().getFilename());
    }

    @Test
    void Give_InvalidDates_WhenSearchAssetsCalled_ShouldTriggerFallbackAndReturnEmptyList() {
        // given: Usamos una fecha inválida para provocar un parseo fallido
        String filename = "";
        String filetype = "";

        // when: Llamamos directamente al método fallbackSearch para simular el comportamiento del fallback
        List<Asset> fallbackResult = assetService.fallbackSearch(
                INVALID_UPLOAD_DATE_START, UPLOAD_DATE_END, filename, filetype, SORT_DIRECTION,
                new RuntimeException(ERROR_PROCESANDO_FECHA)
        );

        // then: Se espera que el fallback devuelva una lista vacía
        assertNotNull(fallbackResult);
        assertTrue(fallbackResult.isEmpty());
    }
}
