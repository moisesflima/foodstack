package br.com.foodstack.produtos.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("SecurityConfig - Testes de Integração")
class SecurityConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private SecurityFilterChain filterChain;

    @Test
    @DisplayName("Deve configurar AuthenticationManager como bean")
    void testAuthenticationManagerBeanExists() {
        assertNotNull(authenticationManager, "AuthenticationManager não foi configurado como bean");
    }

    @Test
    @DisplayName("Deve configurar PasswordEncoder como BCryptPasswordEncoder")
    void testPasswordEncoderBeanExists() {
        assertNotNull(passwordEncoder, "PasswordEncoder não foi configurado como bean");
        assertTrue(passwordEncoder.toString().toLowerCase().contains("bcrypt"),
                "PasswordEncoder deve ser BCryptPasswordEncoder");
    }

    @Test
    @DisplayName("Deve encodar senha corretamente com BCrypt")
    void testPasswordEncodingWorks() {
        // Given
        String plainPassword = "testPassword123";

        // When
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(plainPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(plainPassword, encodedPassword),
                "Senha decodificada não corresponde à senha original");
    }

    @Test
    @DisplayName("Deve falhar ao validar senha incorreta")
    void testPasswordEncodingFailsWithWrongPassword() {
        // Given
        String plainPassword = "testPassword123";
        String wrongPassword = "wrongPassword456";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // When & Then
        assertFalse(passwordEncoder.matches(wrongPassword, encodedPassword),
                "Senha incorreta não deveria corresponder");
    }

    @Test
    @DisplayName("Deve gerar diferentes hashes para mesma senha")
    void testPasswordEncodingGeneratesDifferentHashes() {
        // Given
        String plainPassword = "testPassword123";

        // When
        String hash1 = passwordEncoder.encode(plainPassword);
        String hash2 = passwordEncoder.encode(plainPassword);

        // Then
        assertNotEquals(hash1, hash2, "BCrypt deve gerar hashes diferentes para mesma senha");
        assertTrue(passwordEncoder.matches(plainPassword, hash1));
        assertTrue(passwordEncoder.matches(plainPassword, hash2));
    }

    @Test
    @DisplayName("Deve configurar SecurityFilterChain")
    void testSecurityFilterChainIsConfigured() {
        assertNotNull(filterChain, "SecurityFilterChain não foi configurado");
    }

    @Test
    @DisplayName("PasswordEncoder deve usar salt (BCrypt com rounds configurado)")
    void testPasswordEncoderUsesSalt() {
        // Given
        String password = "testPassword123";

        // When
        String encoded1 = passwordEncoder.encode(password);
        String encoded2 = passwordEncoder.encode(password);

        // Then - BCrypt com salt gera hashes diferentes
        assertNotEquals(encoded1, encoded2);

        // Mas ambos devem fazer match com a senha original
        assertTrue(passwordEncoder.matches(password, encoded1));
        assertTrue(passwordEncoder.matches(password, encoded2));
    }
}
