package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl - Testes Unitários")
class UserDetailsServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private static final String VALID_USERNAME = "admin";
    private static final String ENCODED_PASSWORD = "$2a$10$slYQmyNdGzin7olVN3p/2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMUm";

    @BeforeEach
    void setUp() {
        // Mock será configurado apenas nos testes que o usam
    }

    @Test
    @DisplayName("Deve carregar usuário admin com sucesso")
    void testLoadUserByUsernameSuccess() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertNotNull(userDetails);
        assertEquals(VALID_USERNAME, userDetails.getUsername());
        assertNotNull(userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção para usuário desconhecido")
    void testLoadUserByUsernameNotFound() {
        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown"),
                "Deveria lançar UsernameNotFoundException para usuário desconhecido");
    }

    @Test
    @DisplayName("Deve ter role ADMIN para usuário admin")
    void testAdminUserHasAdminRole() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                "Usuário admin deve ter role ROLE_ADMIN");
    }

    @Test
    @DisplayName("Deve ter conta ativada para usuário admin")
    void testAdminUserIsEnabled() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertTrue(userDetails.isEnabled(), "Usuário admin deve estar ativado");
    }

    @Test
    @DisplayName("Deve ter conta não expirada para usuário admin")
    void testAdminUserIsNotExpired() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertTrue(userDetails.isAccountNonExpired(), "Conta do usuário admin não deveria estar expirada");
    }

    @Test
    @DisplayName("Deve ter credenciais não expiradas para usuário admin")
    void testAdminUserCredentialsNotExpired() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertTrue(userDetails.isCredentialsNonExpired(),
                "Credenciais do usuário admin não deveriam estar expiradas");
    }

    @Test
    @DisplayName("Deve ter account não bloqueado para usuário admin")
    void testAdminUserAccountNotLocked() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertTrue(userDetails.isAccountNonLocked(), "Conta do usuário admin não deveria estar bloqueada");
    }

    @Test
    @DisplayName("Deve retornar senha encodada (não em texto plano)")
    void testUserPasswordIsEncoded() {
        // Given
        when(passwordEncoder.encode("123")).thenReturn(ENCODED_PASSWORD);

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(VALID_USERNAME);

        // Then
        assertNotNull(userDetails.getPassword());
        // Senha encodada começa com $2a$ ou $2b$ (BCrypt)
        assertTrue(userDetails.getPassword().startsWith("$2a$") ||
                   userDetails.getPassword().startsWith("$2b$"),
                "Senha deveria estar em formato BCrypt");
    }

    @Test
    @DisplayName("Deve ser case-sensitive no username")
    void testUsernameIsCaseSensitive() {
        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("ADMIN"),
                "Username deveria ser case-sensitive");
    }

    @Test
    @DisplayName("Deve lançar exceção para username nulo")
    @SuppressWarnings("ConstantConditions")
    void testLoadUserByUsernameWithNull() {
        // When & Then
        assertThrows(Exception.class,
                () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    @DisplayName("Deve lançar exceção para username vazio")
    void testLoadUserByUsernameWithEmptyString() {
        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(""));
    }
}
