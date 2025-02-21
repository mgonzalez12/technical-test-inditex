package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.jwt;

import static org.junit.jupiter.api.Assertions.*;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    public static final  String USERNAME = "testuser";
    public static final  String INVALID_TOKEN = "invalid.token.value";
    public static final  String MESSAGE_TOKEN_GENERADO_NO_DEBE_SER_NULL = "El token generado no debe ser nulo";
    public static final  String MESSAGE_TOKEN_GENERADO_NO_DEBE_SER_VALIDO = "El token generado debe ser válido";
    public static final  String MESSAGE_USERNAME_DEBE_COINCIDIR = "El username extraído debe coincidir con el ingresado";
    public static final  String MESSAGE_TOKEN_INVALIDO = "Un token inválido debe retornar false al validarse";
    public static final  String MESSAGE_USERNAME_TOKEN_MODIFICADO = "Al extraer el username de un token modificado se debe lanzar una excepción";
    private final JwtUtil jwtUtil = new JwtUtil();
    public final  String TOKEN = jwtUtil.generateToken(USERNAME);
    // Instancia real de JwtUtil, cada instancia genera su propia clave secreta.


    @Test
    void Give_ValidUsername_WhenGenerateTokenCalled_ShouldReturnValidTokenAndExtractUsername() {
        // then: El token no debe ser nulo, debe ser válido y al extraer el username debe coincidir
        assertNotNull(TOKEN, MESSAGE_TOKEN_GENERADO_NO_DEBE_SER_NULL);
        assertTrue(jwtUtil.validateToken(TOKEN), MESSAGE_TOKEN_GENERADO_NO_DEBE_SER_VALIDO);
        String extractedUsername = jwtUtil.getUsernameFromToken(TOKEN);
        assertEquals(USERNAME, extractedUsername, MESSAGE_USERNAME_DEBE_COINCIDIR);
    }

    @Test
    void Give_InvalidToken_WhenValidateTokenCalled_ShouldReturnFalse() {
        // when: Se valida el token
        boolean isValid = jwtUtil.validateToken(INVALID_TOKEN);
        // then: La validación debe retornar false
        assertFalse(isValid, MESSAGE_TOKEN_INVALIDO);
    }

    @Test
    void Give_TamperedToken_WhenGetUsernameFromTokenCalled_ShouldThrowException() {
        // Se tampea el token (por ejemplo, modificando el último carácter)
        String tamperedToken = TOKEN.substring(0, TOKEN.length() - 1) + "X";
        // when/then: Al intentar extraer el username de un token alterado, debe lanzarse una excepción (por ejemplo, JwtException)
        assertThrows(JwtException.class, () -> jwtUtil.getUsernameFromToken(tamperedToken),
                MESSAGE_USERNAME_TOKEN_MODIFICADO);
    }
}
