package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class InMemoryUserDetailsServiceConfigTest {

    public static final  String USER = "user";
    public static final  String RAW_PASSWORD = "password";
    public static final  String USUARIO_NOT_NULL = "El usuario no debería ser null";
    public static final  String USERNAME_MESSAGE = "El nombre de usuario debe ser 'user'";
    public static final  String PASSWORD_ENCODE ="La contraseña codificada debe coincidir con la contraseña original";
    public static final  String ROLE_USER = "ROLE_USER";
    public static final  String USER_MUST_HAVE_ROLE ="El usuario debe tener el rol 'ROLE_USER'";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void Give_Lo_que_hace_el_test_UserDetailsService_ShouldLoadUserWithCorrectCredentials() {
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(USER);

        // then
        assertNotNull(userDetails, USUARIO_NOT_NULL);
        assertEquals(USER, userDetails.getUsername(), USERNAME_MESSAGE);
        // Verifica que la contraseña codificada coincida con la original
        assertTrue(passwordEncoder.matches(RAW_PASSWORD, userDetails.getPassword()),
                PASSWORD_ENCODE);
        // Verifica que el usuario tenga el rol 'ROLE_USER'
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals(ROLE_USER)),
                USER_MUST_HAVE_ROLE);
    }
}
