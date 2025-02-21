package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

    public static final  String EXPECTED_MESSAGE = "Resource not found";
    public static final  String MESSAGE = "El mensaje de la excepción debe ser el mismo que se pasó al constructor";
    @Test
    void Give_Message_WhenResourceNotFoundExceptionCreated_ShouldReturnSameMessage() {
        // when: Se crea la excepción con ese mensaje
        ResourceNotFoundException exception = new ResourceNotFoundException(EXPECTED_MESSAGE);

        // then: El mensaje obtenido debe coincidir con el esperado
        assertEquals(EXPECTED_MESSAGE, exception.getMessage(), MESSAGE);
    }
}
