package br.com.foodstack.produtos.security.filter;

import br.com.foodstack.produtos.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - Testes Unitários")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve prosseguir sem autenticação quando não há header Authorization")
    void testNoAuthorizationHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve prosseguir sem autenticação quando header não começa com Bearer")
    void testAuthorizationHeaderWithoutBearer() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve autenticar usuário com token JWT válido")
    void testValidTokenAuthentication() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + VALID_TOKEN;
        UserDetails userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .roles("USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(USERNAME, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("Deve retornar null quando token extraction falha")
    void testTokenExtractionFails() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + VALID_TOKEN;
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername(VALID_TOKEN)).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    @DisplayName("Deve prosseguir quando authentication já existe no SecurityContext")
    void testAuthenticationAlreadyExists() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + VALID_TOKEN;
        UserDetails userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .roles("USER")
                .build();

        // Pré-estabelece uma autenticação
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    @DisplayName("Deve extrair token corretamente do header Authorization")
    void testTokenExtractionFromHeader() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9.token.signature";
        UserDetails userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .roles("USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername("eyJhbGciOiJIUzI1NiJ9.token.signature")).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil).extractUsername("eyJhbGciOiJIUzI1NiJ9.token.signature");
    }

    @Test
    @DisplayName("Deve preservar authorities do usuário na autenticação")
    void testAuthoritiesPreserved() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + VALID_TOKEN;
        UserDetails userDetails = User.builder()
                .username(USERNAME)
                .password("password")
                .roles("ADMIN", "USER")
                .build();

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractUsername(VALID_TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(2, SecurityContextHolder.getContext().getAuthentication().getAuthorities().size());
    }

    @Test
    @DisplayName("Deve ignorar header Authorization inválido")
    void testInvalidAuthorizationFormat() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("BearerInvalid");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve chamar filterChain.doFilter após processamento")
    void testFilterChainContinues() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
