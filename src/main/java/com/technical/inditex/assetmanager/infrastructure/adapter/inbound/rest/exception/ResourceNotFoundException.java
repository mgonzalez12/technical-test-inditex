package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
