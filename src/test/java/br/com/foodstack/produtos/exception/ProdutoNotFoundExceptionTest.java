package br.com.foodstack.produtos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoNotFoundException - Testes Unitários")
class ProdutoNotFoundExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem correta")
    void deveCriarExcecaoComMensagemCorreta() {
        // Arrange & Act
        ProdutoNotFoundException exception = new ProdutoNotFoundException(1L);

        // Assert
        assertNotNull(exception);
        assertEquals("Produto não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com ID diferente")
    void deveCriarExcecaoComIdDiferente() {
        // Arrange & Act
        ProdutoNotFoundException exception = new ProdutoNotFoundException(999L);

        // Assert
        assertNotNull(exception);
        assertEquals("Produto não encontrado com o ID: 999", exception.getMessage());
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void deveSerUmaRuntimeException() {
        // Arrange & Act
        ProdutoNotFoundException exception = new ProdutoNotFoundException(1L);

        // Assert
        assertInstanceOf(RuntimeException.class, exception);
    }
}
