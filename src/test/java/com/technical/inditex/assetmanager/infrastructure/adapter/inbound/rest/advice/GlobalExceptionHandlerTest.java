package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception.BadRequestException;
import com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception.ErrorResponse;
import com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    public static final  String EXEPTION_MESSAGE_RESOURCE = "Resource not found error";
    public static final  String EXEPTION_MESSAGE_BAD_REQUEST = "Bad request error";
    public static final  String EXEPTION_MESSAGE_INTERNAL_ERROR= "Some internal error";
    public static final  String MESSAGE_NOT_NULL= "El cuerpo de la respuesta no debe ser nulo";
    public static final  String MESSAGE_TIME_NOT_NULL= "El timestamp no debe ser nulo";
    public static final  String MESSAGE_INTERNAL_SERVER_ERROR= "Internal Server Error";
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void Give_ResourceNotFoundException_WhenHandleResourceNotFoundCalled_ShouldReturnNotFoundErrorResponse() {
        // given
        ResourceNotFoundException ex = new ResourceNotFoundException(EXEPTION_MESSAGE_RESOURCE);
        WebRequest webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/resource");

        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleResourceNotFound(ex, webRequest);

        // then
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse, MESSAGE_NOT_NULL);
        assertNotNull(errorResponse.getTimestamp(), MESSAGE_TIME_NOT_NULL);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals(EXEPTION_MESSAGE_RESOURCE, errorResponse.getMessage());
        assertEquals("/resource", errorResponse.getPath());
    }

    @Test
    void Give_BadRequestException_WhenHandleBadRequestCalled_ShouldReturnBadRequestErrorResponse() {
        // given
        BadRequestException ex = new BadRequestException(EXEPTION_MESSAGE_BAD_REQUEST);
        WebRequest webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/bad");

        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleBadRequest(ex, webRequest);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse, MESSAGE_NOT_NULL);
        assertNotNull(errorResponse.getTimestamp(), MESSAGE_TIME_NOT_NULL);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals(EXEPTION_MESSAGE_BAD_REQUEST, errorResponse.getMessage());
        assertEquals("/bad", errorResponse.getPath());
    }

    @Test
    void Give_GenericException_WhenHandleAllExceptionsCalled_ShouldReturnInternalServerErrorResponse() {
        // given
        Exception ex = new Exception(EXEPTION_MESSAGE_INTERNAL_ERROR);
        WebRequest webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/general");

        // when
        ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleAllExceptions(ex, webRequest);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse, MESSAGE_NOT_NULL);
        assertNotNull(errorResponse.getTimestamp(), MESSAGE_TIME_NOT_NULL);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals(MESSAGE_INTERNAL_SERVER_ERROR, errorResponse.getError());
        assertEquals(EXEPTION_MESSAGE_INTERNAL_ERROR, errorResponse.getMessage());
        assertEquals("/general", errorResponse.getPath());
    }
}
