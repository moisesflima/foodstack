package br.com.foodstack.produtos.controller;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.exception.ProdutoNotFoundException;
import br.com.foodstack.produtos.service.impl.ProdutoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoController - Testes Unitários")
class ProdutoControllerTest {

    @Mock
    private ProdutoServiceImpl produtoServiceImpl;

    @InjectMocks
    private ProdutoController produtoController;

    private ProdutoResponseDTO responseDTO;
    private ProdutoRequestDTO requestDTO;
    private PageResponseDTO<ProdutoResponseDTO> pageResponseDTO;

    private Validator validator;

    @BeforeEach
    void setUp() {
        responseDTO = new ProdutoResponseDTO(
                1L,
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                "http://exemplo.com/imagem.jpg",
                true,
                20,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestDTO = new ProdutoRequestDTO(
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                "http://exemplo.com/imagem.jpg",
                true,
                20
        );

        pageResponseDTO = new PageResponseDTO<>(
                List.of(responseDTO),
                1,
                1L
        );

        // Inicializa Validator para uso nos testes de validação manual
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("GET /api/produtos - Deve listar todos os produtos com paginação")
    void deveListarTodosProdutosComPaginacao() {
        // Arrange
        when(produtoServiceImpl.listarTodos(any(Pageable.class))).thenReturn(pageResponseDTO);

        // Act
        ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> responseEntity = produtoController.listarTodos(0, 10);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PageResponseDTO<ProdutoResponseDTO> result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals(1, result.totalPages());
        assertEquals(1L, result.totalElements());
        assertFalse(result.content().isEmpty());
        verify(produtoServiceImpl, times(1)).listarTodos(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/produtos - Deve usar valores padrão de paginação")
    void deveUsarValoresPadraoDePaginacao() {
        // Arrange
        when(produtoServiceImpl.listarTodos(any(Pageable.class))).thenReturn(pageResponseDTO);

        // Act
        ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> responseEntity = produtoController.listarTodos(0, 10);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(produtoServiceImpl, times(1)).listarTodos(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/produtos/{id} - Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        // Arrange
        when(produtoServiceImpl.buscarPorId(1L)).thenReturn(responseDTO);

        // Act
        ResponseEntity<ProdutoResponseDTO> responseEntity = produtoController.buscarPorId(1L);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ProdutoResponseDTO result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Hambúrguer Gourmet", result.nome());
        assertEquals(new BigDecimal("35.00"), result.preco());
        verify(produtoServiceImpl, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404QuandoProdutoNaoExiste() {
        // Arrange
        when(produtoServiceImpl.buscarPorId(999L)).thenThrow(new ProdutoNotFoundException(999L));

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class,
                () -> produtoController.buscarPorId(999L));
        verify(produtoServiceImpl, times(1)).buscarPorId(999L);
    }

    @Test
    @DisplayName("GET /api/produtos/categoria/{categoria} - Deve buscar por categoria")
    void deveBuscarProdutosPorCategoria() {
        // Arrange
        when(produtoServiceImpl.buscarPorCategoria(eq(Categoria.LANCHE), any(Pageable.class)))
                .thenReturn(pageResponseDTO);

        // Act
        ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> responseEntity = produtoController.buscarPorCategoria(Categoria.LANCHE, 0, 10);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PageResponseDTO<ProdutoResponseDTO> result = responseEntity.getBody();
        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals("LANCHE", result.content().get(0).categoria().toString());
        verify(produtoServiceImpl, times(1)).buscarPorCategoria(eq(Categoria.LANCHE), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/produtos/restaurante/{restauranteId} - Deve buscar por restaurante")
    void deveBuscarProdutosPorRestaurante() {
        // Arrange
        when(produtoServiceImpl.buscarPorRestaurante(eq(1L), any(Pageable.class)))
                .thenReturn(pageResponseDTO);

        // Act
        ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> responseEntity = produtoController.buscarPorRestaurante(1L, 0, 10);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PageResponseDTO<ProdutoResponseDTO> result = responseEntity.getBody();
        assertNotNull(result);
        assertFalse(result.content().isEmpty());
        assertEquals(1L, result.content().get(0).restauranteId());
        verify(produtoServiceImpl, times(1)).buscarPorRestaurante(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("POST /api/produtos - Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        // Arrange
        when(produtoServiceImpl.criar(any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<ProdutoResponseDTO> responseEntity = produtoController.criar(requestDTO);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ProdutoResponseDTO result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals("Hambúrguer Gourmet", result.nome());
        assertEquals(new BigDecimal("35.00"), result.preco());
        verify(produtoServiceImpl, times(1)).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar erro quando dados inválidos")
    void deveRetornarErroQuandoDadosInvalidos() {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "",  // nome vazio - inválido
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(requestInvalido);

        // Assert
        assertFalse(violations.isEmpty());
        verify(produtoServiceImpl, never()).criar(any());
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar erro quando preço negativo")
    void deveRetornarErroQuandoPrecoNegativo() {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "Produto",
                "Descrição",
                new BigDecimal("-10.00"),  // preço negativo - inválido
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(requestInvalido);

        // Assert
        assertFalse(violations.isEmpty());
        verify(produtoServiceImpl, never()).criar(any());
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar erro quando restauranteId null")
    void deveRetornarErroQuandoRestauranteIdNull() {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "Produto",
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                null,  // restauranteId null - inválido
                null,
                true,
                20
        );

        // Act
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(requestInvalido);

        // Assert
        assertFalse(violations.isEmpty());
        verify(produtoServiceImpl, never()).criar(any());
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        when(produtoServiceImpl.atualizar(eq(1L), any(ProdutoRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act
        ResponseEntity<ProdutoResponseDTO> responseEntity = produtoController.atualizar(1L, requestDTO);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        ProdutoResponseDTO result = responseEntity.getBody();
        assertNotNull(result);
        assertEquals("Hambúrguer Gourmet", result.nome());
        verify(produtoServiceImpl, times(1)).atualizar(eq(1L), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404AoAtualizarProdutoInexistente() {
        // Arrange
        when(produtoServiceImpl.atualizar(eq(999L), any(ProdutoRequestDTO.class)))
                .thenThrow(new ProdutoNotFoundException(999L));

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class,
                () -> produtoController.atualizar(999L, requestDTO));
        verify(produtoServiceImpl, times(1)).atualizar(eq(999L), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve retornar erro quando dados inválidos")
    void deveRetornarErroAoAtualizarComDadosInvalidos() {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "",  // nome vazio - inválido
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act
        Set<ConstraintViolation<ProdutoRequestDTO>> violations = validator.validate(requestInvalido);

        // Assert
        assertFalse(violations.isEmpty());
        verify(produtoServiceImpl, never()).atualizar(anyLong(), any());
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        // Arrange
        doNothing().when(produtoServiceImpl).deletar(1L);

        // Act
        ResponseEntity<Void> responseEntity = produtoController.deletar(1L);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(produtoServiceImpl, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404AoDeletarProdutoInexistente() {
        // Arrange
        doThrow(new ProdutoNotFoundException(999L)).when(produtoServiceImpl).deletar(999L);

        // Act & Assert
        assertThrows(ProdutoNotFoundException.class,
                () -> produtoController.deletar(999L));
        verify(produtoServiceImpl, times(1)).deletar(999L);
    }
}

