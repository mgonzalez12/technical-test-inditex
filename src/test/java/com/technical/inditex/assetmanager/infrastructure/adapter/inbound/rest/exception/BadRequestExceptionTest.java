package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class BadRequestExceptionTest {

    public static final  String TEST_ERROR_MESSAGE = "Test error message";
    public static final  String MESSAGE = "El mensaje de la excepción debe ser el mismo que se pasó al constructor";

    @Test
    void Give_Message_WhenBadRequestExceptionCreated_ShouldReturnSameMessage() {
        // when: se crea la excepción con ese mensaje
        BadRequestException exception = new BadRequestException(TEST_ERROR_MESSAGE);
        // then: el mensaje obtenido debe coincidir con el esperado
        assertEquals(TEST_ERROR_MESSAGE, exception.getMessage(), MESSAGE);
    }
}
