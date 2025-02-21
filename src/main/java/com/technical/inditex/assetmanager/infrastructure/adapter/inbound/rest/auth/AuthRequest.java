package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.auth;

import lombok.Data;

@Data
class AuthRequest {
    private String username;
    private String password;
}
