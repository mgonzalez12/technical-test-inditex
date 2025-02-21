package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

class ErrorResponseTest {

    public static final  String NOT_FOUND = "Not Found";
    public static final  String MESSAGE_NOT_FOUND = "Resource not found";
    public static final  String PATH = "/api/test";
    public static final  String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final  String UNEXPECTD_ERROR = "Unexpected error";

    @Test
    void Give_NoArgsConstructor_WhenSettersCalled_ShouldReturnExpectedValues() {
        // given: Se crea una instancia usando el constructor sin argumentos
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        // when: Se establecen los valores mediante setters
        errorResponse.setTimestamp(now);
        errorResponse.setStatus(404);
        errorResponse.setError(NOT_FOUND);
        errorResponse.setMessage(MESSAGE_NOT_FOUND);
        errorResponse.setPath(PATH);

        // then: Los getters deben retornar los valores asignados
        assertEquals(now, errorResponse.getTimestamp());
        assertEquals(404, errorResponse.getStatus());
        assertEquals(NOT_FOUND, errorResponse.getError());
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.getMessage());
        assertEquals(PATH, errorResponse.getPath());
    }

    @Test
    void Give_AllArgsConstructor_WhenInstanceCreated_ShouldReturnExpectedValues() {
        // given: Se establece un timestamp y se crea una instancia usando el constructor con todos los argumentos
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(now, 500, INTERNAL_SERVER_ERROR, UNEXPECTD_ERROR, PATH);

        // then: Los getters deben retornar los valores pasados al constructor
        assertEquals(now, errorResponse.getTimestamp());
        assertEquals(500, errorResponse.getStatus());
        assertEquals(INTERNAL_SERVER_ERROR, errorResponse.getError());
        assertEquals(UNEXPECTD_ERROR, errorResponse.getMessage());
        assertEquals(PATH, errorResponse.getPath());
    }
}
