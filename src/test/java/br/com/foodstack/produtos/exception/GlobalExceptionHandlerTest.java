package br.com.foodstack.produtos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler - Testes Unitários")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve tratar ProdutoNotFoundException e retornar 404")
    void deveTratarProdutoNotFoundException() {
        // Arrange
        ProdutoNotFoundException exception = new ProdutoNotFoundException(1L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/produtos/1");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleProdutoNotFound(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Produto não encontrado com o ID: 1", response.getBody().message());
        assertEquals(404, response.getBody().status());
        assertEquals("/api/produtos/1", response.getBody().path());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException e retornar 400 com erros de validação")
    void deveTratarMethodArgumentNotValidException() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("produtoRequestDTO", "nome", "O nome é obrigatório");
        FieldError fieldError2 = new FieldError("produtoRequestDTO", "preco", "O preço deve ser positivo");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/produtos");
        
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));
        
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                exceptionHandler.handleValidationExceptions(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(2, errors.size());
        assertEquals("O nome é obrigatório", errors.get("nome"));
        assertEquals("O preço deve ser positivo", errors.get("preco"));
        assertEquals("/api/produtos", response.getBody().get("path"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica e retornar 500")
    void deveTratarExceptionGenerica() {
        // Arrange
        Exception exception = new RuntimeException("Erro inesperado");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/produtos");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
                exceptionHandler.handleGenericException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocorreu um erro inesperado no servidor", response.getBody().message());
        assertEquals(500, response.getBody().status());
        assertEquals("/api/produtos", response.getBody().path());
    }
}
