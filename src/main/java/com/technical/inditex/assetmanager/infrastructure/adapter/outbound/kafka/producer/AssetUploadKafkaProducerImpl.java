package com.technical.inditex.assetmanager.infrastructure.adapter.outbound.kafka.producer;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssetUploadKafkaProducerImpl implements AssetUploadKafkaProducer {

    private static final String TOPIC = "asset-upload";
    private static final String MESSAGE_KAFKA = "Interrupted while sending message to Kafka";
    private static final String THREAD_INTERRUPTED ="Thread interrupted during retry sleep";
    private static final String MESSAGE_ERROR ="No se pudo enviar el mensaje a Kafka tras varios intentos";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public AssetUploadKafkaProducerImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendAssetUploadMessage(String assetId, String encodedFile) {
        String message = "{\"assetId\":\"" + assetId + "\",\"encodedFile\":\"" + encodedFile + "\"}";
        int attempts = 0;
        boolean sent = false;
        while (attempts < 3 && !sent) {
            try {
                kafkaTemplate.send(TOPIC, assetId, message).get();
                sent = true;
            } catch (Exception e) {
                // Si la causa es InterruptedException, interrumpimos y lanzamos la RuntimeException inmediatamente.
                if (e.getCause() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(MESSAGE_KAFKA, e);
                }
                attempts++;
                try {
                    Thread.sleep(1000); // espera antes de reintentar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(THREAD_INTERRUPTED, ie);
                }
            }
        }
        if (!sent) {
            throw new RuntimeException(MESSAGE_ERROR);
        }
    }
}
