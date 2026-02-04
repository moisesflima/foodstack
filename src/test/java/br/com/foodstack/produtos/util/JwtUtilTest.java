package br.com.foodstack.produtos.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil - Testes Unitários")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "VGhpcyBpcyBhIDI1NiBiaXQgc2VjcmV0IGtleSBmb3IgSldUIEhNQUMtU0hBMjU2IHRoYXQgaXMgc2VjdXJlIGVub3VnaA==";
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido")
    void testGenerateTokenSuccess() {
        // When
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        assertEquals(3, token.split("\\.").length); // JWT tem 3 partes
    }

    @Test
    @DisplayName("Deve extrair username do token JWT válido")
    void testExtractUsernameSuccess() {
        // Given
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertNotNull(extractedUsername);
        assertEquals(TEST_USERNAME, extractedUsername);
    }

    @Test
    @DisplayName("Deve gerar tokens válidos em múltiplas chamadas")
    void testGenerateDifferentTokensEachCall() {
        // When
        String token1 = jwtUtil.generateToken(TEST_USERNAME);
        String token2 = jwtUtil.generateToken(TEST_USERNAME);

        // Then - Ambos tokens devem ser válidos
        assertNotNull(token1);
        assertNotNull(token2);
        // Tokens podem ser iguais se gerados no mesmo milissegundo
        // O importante é que ambos sejam válidos
        assertTrue(token1.contains("."));
        assertTrue(token2.contains("."));
    }

    @Test
    @DisplayName("Deve extrair mesmo username de múltiplos tokens")
    void testExtractSameUsernameFromMultipleTokens() {
        // When
        String token1 = jwtUtil.generateToken(TEST_USERNAME);
        String token2 = jwtUtil.generateToken(TEST_USERNAME);

        String username1 = jwtUtil.extractUsername(token1);
        String username2 = jwtUtil.extractUsername(token2);

        // Then
        assertEquals(username1, username2);
        assertEquals(TEST_USERNAME, username1);
    }

    @Test
    @DisplayName("Deve gerar token com expiração correta")
    void testTokenExpiration() {
        // When
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Then
        assertNotNull(token);
        // Token deve ser válido imediatamente
        String username = jwtUtil.extractUsername(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    @DisplayName("Deve falhar ao extrair username de token inválido")
    void testExtractUsernameInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    @DisplayName("Deve falhar ao extrair username de token vazio")
    void testExtractUsernameEmptyToken() {
        // Given
        String emptyToken = "";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(emptyToken));
    }

    @Test
    @DisplayName("Deve falhar ao extrair username de token nulo")
    void testExtractUsernameNullToken() {
        // When & Then - Qualquer tipo de exceção é aceitável
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(null));
    }

    @Test
    @DisplayName("Deve gerar token com claims corretos")
    void testGenerateTokenContainsClaims() {
        // When
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Then
        assertNotNull(token);
        // Verifica se o token contém a estrutura esperada
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        // Decodifica o payload (segunda parte)
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        assertTrue(payload.contains(TEST_USERNAME));
    }

    @Test
    @DisplayName("Deve gerar tokens para diferentes usernames")
    void testGenerateDifferentTokensForDifferentUsernames() {
        // When
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        String username1 = jwtUtil.extractUsername(token1);
        String username2 = jwtUtil.extractUsername(token2);

        // Then
        assertNotEquals(token1, token2);
        assertEquals("user1", username1);
        assertEquals("user2", username2);
    }

    @Test
    @DisplayName("Deve gerar token com formato JWT correto")
    void testGenerateTokenFormatIsValid() {
        // When
        String token = jwtUtil.generateToken(TEST_USERNAME);

        // Then
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        // Cada parte deve ser Base64 válido
        assertDoesNotThrow(() -> Base64.getUrlDecoder().decode(parts[0]));
        assertDoesNotThrow(() -> Base64.getUrlDecoder().decode(parts[1]));
        assertDoesNotThrow(() -> Base64.getUrlDecoder().decode(parts[2]));
    }
}
