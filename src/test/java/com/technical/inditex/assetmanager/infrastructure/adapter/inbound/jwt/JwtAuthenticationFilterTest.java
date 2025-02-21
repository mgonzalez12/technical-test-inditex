package com.technical.inditex.assetmanager.infrastructure.adapter.inbound.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    public static final  String INVALID_TOKEN = "invalid-token";
    public static final  String AUTHORIZATION = "Authorization";
    public static final  String BEARER = "Bearer ";
    public static final  String VALID_TOKEN = "valid-token";
    public static final  String USERNAME = "testuser";

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // Aseguramos limpiar el contexto de seguridad antes de cada test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void Given_NoAuthorizationHeader_WhenDoFilterInternalCalled_ShouldNotSetAuthentication() throws ServletException, IOException {
        // given: Se crea una petición sin header de autorización
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when: Se ejecuta el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then: No se debe establecer autenticación en el SecurityContext y se invoca el filterChain
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void Given_InvalidToken_WhenDoFilterInternalCalled_ShouldNotSetAuthentication() throws ServletException, IOException {
        // given: Se crea una petición con un token inválido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, BEARER + INVALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.validateToken(INVALID_TOKEN)).thenReturn(false);

        // when: Se ejecuta el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then: No se establece autenticación y se invoca el filterChain
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, times(1)).validateToken(INVALID_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void Given_ValidTokenAndNoAuthentication_WhenDoFilterInternalCalled_ShouldSetAuthentication() throws ServletException, IOException {
        // given: Se crea una petición con un token válido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, BEARER + VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);

        // given: Simulamos que el UserDetails se carga correctamente
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // when: Se ejecuta el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then: Se establece autenticación en el SecurityContext
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        UsernamePasswordAuthenticationToken authToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userDetails, authToken.getPrincipal());
        assertNotNull(authToken.getDetails());
        verify(jwtUtil, times(1)).validateToken(VALID_TOKEN);
        verify(jwtUtil, times(1)).getUsernameFromToken(VALID_TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(USERNAME);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void Given_ValidTokenAndExistingAuthentication_WhenDoFilterInternalCalled_ShouldNotOverrideAuthentication() throws ServletException, IOException {
        // given: Se crea una petición con un token válido y se establece previamente una autenticación
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION, BEARER + VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Preestablecemos una autenticación en el contexto
        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken("existingUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        // Aunque el token sea válido, el filtro no debe reemplazar la autenticación existente
        when(jwtUtil.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(VALID_TOKEN)).thenReturn(USERNAME);

        // when: Se ejecuta el filtro
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then: La autenticación en el SecurityContext debe permanecer igual
        assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
