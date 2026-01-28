package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.exception.ProdutoNotFoundException;
import br.com.foodstack.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - Testes Unitários")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequestDTO requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Hambúrguer Gourmet");
        produto.setDescricao("Pão brioche, carne 180g");
        produto.setPreco(new BigDecimal("35.00"));
        produto.setCategoria(Categoria.LANCHE);
        produto.setRestauranteId(1L);
        produto.setImagemUrl("http://exemplo.com/imagem.jpg");
        produto.setDisponivel(true);
        produto.setTempoPreparoMinutos(20);
        produto.setDataCriacao(LocalDateTime.now());
        produto.setDataAtualizacao(LocalDateTime.now());

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

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve listar todos os produtos com paginação")
    void deveListarTodosProdutosComPaginacao() {
        // Arrange
        List<Produto> produtos = List.of(produto);
        Page<Produto> page = new PageImpl<>(produtos, pageable, 1);
        when(produtoRepository.findAll(pageable)).thenReturn(page);

        // Act
        PageResponseDTO<ProdutoResponseDTO> resultado = produtoService.listarTodos(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.content().size());
        assertEquals(1, resultado.totalPages());
        assertEquals(1, resultado.totalElements());
        assertEquals("Hambúrguer Gourmet", resultado.content().get(0).nome());
        verify(produtoRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Act
        ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Hambúrguer Gourmet", resultado.nome());
        assertEquals(new BigDecimal("35.00"), resultado.preco());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente por ID")
    void deveLancarExcecaoAoBuscarProdutoInexistentePorId() {
        // Arrange
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ProdutoNotFoundException exception = assertThrows(
                ProdutoNotFoundException.class,
                () -> produtoService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(produtoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria com paginação")
    void deveBuscarProdutosPorCategoriaComPaginacao() {
        // Arrange
        List<Produto> produtos = List.of(produto);
        Page<Produto> page = new PageImpl<>(produtos, pageable, 1);
        when(produtoRepository.findByCategoria(Categoria.LANCHE, pageable)).thenReturn(page);

        // Act
        PageResponseDTO<ProdutoResponseDTO> resultado = produtoService.buscarPorCategoria(Categoria.LANCHE, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.content().size());
        assertEquals(Categoria.LANCHE, resultado.content().get(0).categoria());
        verify(produtoRepository, times(1)).findByCategoria(Categoria.LANCHE, pageable);
    }

    @Test
    @DisplayName("Deve buscar produtos por restaurante com paginação")
    void deveBuscarProdutosPorRestauranteComPaginacao() {
        // Arrange
        List<Produto> produtos = List.of(produto);
        Page<Produto> page = new PageImpl<>(produtos, pageable, 1);
        when(produtoRepository.findByRestauranteId(1L, pageable)).thenReturn(page);

        // Act
        PageResponseDTO<ProdutoResponseDTO> resultado = produtoService.buscarPorRestaurante(1L, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.content().size());
        assertEquals(1L, resultado.content().get(0).restauranteId());
        verify(produtoRepository, times(1)).findByRestauranteId(1L, pageable);
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.criar(requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Hambúrguer Gourmet", resultado.nome());
        assertEquals(new BigDecimal("35.00"), resultado.preco());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve criar produto com disponivel null (default true)")
    void deveCriarProdutoComDisponivelNull() {
        // Arrange
        ProdutoRequestDTO requestComDisponivelNull = new ProdutoRequestDTO(
                "Pizza",
                "Calabresa",
                new BigDecimal("45.00"),
                Categoria.PIZZA,
                2L,
                null,
                null,  // disponivel null
                30
        );
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.criar(requestComDisponivelNull);

        // Assert
        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizar(1L, requestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("Hambúrguer Gourmet", resultado.nome());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com disponivel null")
    void deveAtualizarProdutoComDisponivelNull() {
        // Arrange
        ProdutoRequestDTO requestComDisponivelNull = new ProdutoRequestDTO(
                "Pizza Atualizada",
                "Calabresa",
                new BigDecimal("45.00"),
                Categoria.PIZZA,
                2L,
                null,
                null,  // disponivel null - não deve alterar
                30
        );
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        // Act
        ProdutoResponseDTO resultado = produtoService.atualizar(1L, requestComDisponivelNull);

        // Assert
        assertNotNull(resultado);
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        // Arrange
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ProdutoNotFoundException exception = assertThrows(
                ProdutoNotFoundException.class,
                () -> produtoService.atualizar(999L, requestDTO)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(produtoRepository, times(1)).findById(999L);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        // Arrange
        when(produtoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(produtoRepository).deleteById(1L);

        // Act
        produtoService.deletar(1L);

        // Assert
        verify(produtoRepository, times(1)).existsById(1L);
        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        // Arrange
        when(produtoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        ProdutoNotFoundException exception = assertThrows(
                ProdutoNotFoundException.class,
                () -> produtoService.deletar(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(produtoRepository, times(1)).existsById(999L);
        verify(produtoRepository, never()).deleteById(anyLong());
    }
}
