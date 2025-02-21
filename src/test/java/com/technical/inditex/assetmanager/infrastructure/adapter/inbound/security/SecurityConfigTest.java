package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    public static final  String URL_SWAGGER = "/swagger-ui/index.html";
    public static final  String URL_AUTH = "/auth/login";
    public static final  String URL_ASSETS ="/api/mgmt/1/assets/";
    public static final  String REQUEST_BODY = "{\"username\":\"user\", \"password\":\"password\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void Give_PublicEndpoint_WhenAccessSwaggerUi_ShouldReturnOk() throws Exception {
        // when: se realiza una petición GET a la URL
        mockMvc.perform(get(URL_SWAGGER))
                // then: se espera HTTP 200 OK
                .andExpect(status().isOk());
    }

    @Test
    void Give_PublicEndpoint_WhenAccessAuthLogin_ShouldReturnOk() throws Exception {
        // when: se realiza una petición POST a /auth/login con credenciales válidas
        mockMvc.perform(post(URL_AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST_BODY))
                // then: se espera HTTP 200 OK
                .andExpect(status().isOk());
    }

    @Test
    void Give_ProtectedEndpoint_WhenNoAuthProvided_ShouldReturnUnauthorized() throws Exception {
        // when: se realiza una petición GET sin token de autenticación
        mockMvc.perform(get(URL_ASSETS))
                // then: se espera HTTP 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }
}
