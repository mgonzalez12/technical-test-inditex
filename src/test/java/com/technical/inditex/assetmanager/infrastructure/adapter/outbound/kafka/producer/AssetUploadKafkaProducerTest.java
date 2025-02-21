package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;


@ExtendWith(MockitoExtension.class)
public class AssetUploadKafkaProducerTest {

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
    void Give_ValidMessage_WhenSendAssetUploadMessageIsCalled_ShouldSendMessageToKafkaSuccessfully() throws Exception {
        // given
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(ASSET_UPLOAD), eq(assetId), eq(expectedMessage)))
                .thenReturn(future);
        // when
        producer.sendAssetUploadMessage(assetId, encodedFile);
        // then
        verify(kafkaTemplate, times(1)).send(ASSET_UPLOAD, assetId, expectedMessage);
    }
}
