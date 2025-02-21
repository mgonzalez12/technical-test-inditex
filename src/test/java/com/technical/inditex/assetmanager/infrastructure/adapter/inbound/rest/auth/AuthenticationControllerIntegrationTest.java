package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.rest.auth;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Given valid credentials,
     * When the login endpoint is called,
     * Then it should return a 200 OK response with a token.
     */
    @Test
    void Give_ValidCredentials_WhenLoginCalled_ShouldReturnTokenInResponse() throws Exception {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("password");

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    /**
     * Given invalid credentials,
     * When the login endpoint is called,
     * Then it should return a 401 Unauthorized response.
     */
    @Test
    void Give_InvalidCredentials_WhenLoginCalled_ShouldReturnUnauthorized() throws Exception {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("wrongpassword");

        // when & then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
