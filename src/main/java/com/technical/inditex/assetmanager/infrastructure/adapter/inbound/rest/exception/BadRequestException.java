package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}