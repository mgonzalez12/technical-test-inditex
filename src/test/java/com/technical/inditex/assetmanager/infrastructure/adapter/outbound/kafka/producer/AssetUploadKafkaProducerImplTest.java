package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
class AssetUploadKafkaProducerImplTest {

    public static final  String TEST_ASSET_ID =  "test-asset-id";
    public static final  String TEST_ENDODED_FILE =  "test-encoded-file";
    public static final  String ASSET_UPLOAD = "asset-upload";
    private String assetId;
    private String encodedFile;
    private String expectedMessage;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private AssetUploadKafkaProducerImpl producer;


    @BeforeEach
    void setUp() {
        // given
        assetId = TEST_ASSET_ID;
        encodedFile = TEST_ENDODED_FILE;
        expectedMessage = "{\"assetId\":\"" + assetId + "\",\"encodedFile\":\"" + encodedFile + "\"}";
    }

    @Test
    void GiveSendMessageSuccess() throws Exception {
        // given
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(ASSET_UPLOAD), eq(assetId), eq(expectedMessage)))
                .thenReturn(future);
        // when
        producer.sendAssetUploadMessage(assetId, encodedFile);
        // then
        verify(kafkaTemplate, times(1)).send(ASSET_UPLOAD, assetId, expectedMessage);
    }

    @Test
    void GiveSendMessageRetrySuccess() throws Exception {
        // given
        CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
        failureFuture.completeExceptionally(new RuntimeException("Simulated error"));
        CompletableFuture<SendResult<String, String>> successFuture = CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq(ASSET_UPLOAD), eq(assetId), eq(expectedMessage)))
                .thenReturn(failureFuture)
                .thenReturn(successFuture);
        // when
        producer.sendAssetUploadMessage(assetId, encodedFile);
        // then
        verify(kafkaTemplate, times(2)).send(ASSET_UPLOAD, assetId, expectedMessage);
    }

    @Test
    void GiveSendMessageFailureAfterRetries_ShouldThrow() throws Exception {
        // given
        CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
        failureFuture.completeExceptionally(new RuntimeException("Simulated error"));

        when(kafkaTemplate.send(eq(ASSET_UPLOAD), eq(assetId), eq(expectedMessage)))
                .thenReturn(failureFuture)
                .thenReturn(failureFuture)
                .thenReturn(failureFuture);

        // when & then: se espera que se lance RuntimeException tras 3 intentos
        assertThrows(RuntimeException.class, () -> producer.sendAssetUploadMessage(assetId, encodedFile));
        verify(kafkaTemplate, times(3)).send(ASSET_UPLOAD, assetId, expectedMessage);
    }

    @Test
    void GiveInterruptedException() throws Exception {
        // given
        CompletableFuture<SendResult<String, String>> failureFuture = new CompletableFuture<>();
        failureFuture.completeExceptionally(new InterruptedException("Simulated interruption"));

        when(kafkaTemplate.send(eq(ASSET_UPLOAD), eq(assetId), eq(expectedMessage)))
                .thenReturn(failureFuture);

        // when & then: se espera que se lance RuntimeException y se haga un único intento
        assertThrows(RuntimeException.class, () -> producer.sendAssetUploadMessage(assetId, encodedFile));
        verify(kafkaTemplate, times(1)).send(ASSET_UPLOAD, assetId, expectedMessage);
    }
}
