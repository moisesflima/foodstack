package br.com.foodstack.produtos.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para a API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata a exceção ProdutoNotFoundException.
     *
     * @param ex Exceção lançada.
     * @param request Requisição HTTP.
     * @return Resposta com status 404.
     */
    @ExceptionHandler(ProdutoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProdutoNotFound(ProdutoNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata erros de validação (@Valid).
     *
     * @param ex Exceção lançada.
     * @param request Requisição HTTP.
     * @return Resposta com status 400 e detalhes dos erros.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Erro de Validação");
        body.put("message", "Um ou mais campos estão inválidos");
        body.put("path", request.getRequestURI());
        body.put("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Trata exceções genéricas.
     *
     * @param ex Exceção lançada.
     * @param request Requisição HTTP.
     * @return Resposta com status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno",
                "Ocorreu um erro inesperado no servidor",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Record para representar a estrutura de resposta de erro.
     */
    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}
}
