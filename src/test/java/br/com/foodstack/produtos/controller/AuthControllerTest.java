package br.com.foodstack.produtos.controller;

import br.com.foodstack.produtos.dto.AuthRequest;
import br.com.foodstack.produtos.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Testes Unitários")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private AuthRequest validAuthRequest;
    private AuthRequest invalidAuthRequest;

    @BeforeEach
    void setUp() {
        validAuthRequest = new AuthRequest("admin", "123");
        invalidAuthRequest = new AuthRequest("admin", "wrongpassword");
    }

    @Test
    @DisplayName("Deve retornar ResponseEntity com token quando credenciais são válidas")
    void testLoginSuccess() {
        // Given
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.token.signature";
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("admin", null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtUtil.generateToken("admin"))
                .thenReturn(expectedToken);

        // When
        var response = authController.login(validAuthRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken("admin");
    }

    @Test
    @DisplayName("Deve retornar 401 quando credenciais são inválidas")
    void testLoginFailureInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When
        var response = authController.login(invalidAuthRequest);

        // Then
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar 401 quando usuário não é encontrado")
    void testLoginFailureUserNotFound() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("User not found") {});

        // When
        var response = authController.login(new AuthRequest("unknown", "123"));

        // Then
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Deve validar credenciais com AuthenticationManager")
    void testAuthenticationManagerIsCalled() {
        // Given
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("admin", null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtUtil.generateToken("admin"))
                .thenReturn("token");

        // When
        authController.login(validAuthRequest);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Deve gerar token JWT ao autenticar com sucesso")
    void testJwtTokenIsGenerated() {
        // Given
        String expectedToken = "eyJhbGciOiJIUzI1NiJ9.token.signature";
        Authentication mockAuth = new UsernamePasswordAuthenticationToken("admin", null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtUtil.generateToken("admin"))
                .thenReturn(expectedToken);

        // When
        authController.login(validAuthRequest);

        // Then
        verify(jwtUtil, times(1)).generateToken("admin");
    }
}
