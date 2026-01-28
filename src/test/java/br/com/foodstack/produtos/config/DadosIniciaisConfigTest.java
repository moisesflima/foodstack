package br.com.foodstack.produtos.config;

import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DadosIniciaisConfig - Testes Unitários")
class DadosIniciaisConfigTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private DadosIniciaisConfig dadosIniciaisConfig;

    @Captor
    private ArgumentCaptor<List<Produto>> produtosCaptor;

    @Test
    @DisplayName("Deve popular banco de dados quando estiver vazio")
    void devePopularBancoDeDadosQuandoEstiverVazio() {
        // Arrange
        when(produtoRepository.count()).thenReturn(0L);

        // Act
        dadosIniciaisConfig.run();

        // Assert
        verify(produtoRepository, times(1)).count();
        verify(produtoRepository, times(1)).saveAll(produtosCaptor.capture());
        
        List<Produto> produtosSalvos = produtosCaptor.getValue();
        assertEquals(10, produtosSalvos.size());
        
        // Verificar alguns produtos específicos
        assertTrue(produtosSalvos.stream().anyMatch(p -> "Coca-Cola 350ml".equals(p.getNome())));
        assertTrue(produtosSalvos.stream().anyMatch(p -> "Hambúrguer Gourmet".equals(p.getNome())));
        assertTrue(produtosSalvos.stream().anyMatch(p -> "Pizza Calabresa".equals(p.getNome())));
    }

    @Test
    @DisplayName("Não deve popular banco de dados quando já tiver dados")
    void naoDevePopularBancoDeDadosQuandoJaTiverDados() {
        // Arrange
        when(produtoRepository.count()).thenReturn(5L);

        // Act
        dadosIniciaisConfig.run();

        // Assert
        verify(produtoRepository, times(1)).count();
        verify(produtoRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve criar produtos com todos os campos preenchidos")
    void deveCriarProdutosComTodosCamposPreenchidos() {
        // Arrange
        when(produtoRepository.count()).thenReturn(0L);

        // Act
        dadosIniciaisConfig.run();

        // Assert
        verify(produtoRepository, times(1)).saveAll(produtosCaptor.capture());
        
        List<Produto> produtosSalvos = produtosCaptor.getValue();
        
        // Verificar que todos os produtos têm os campos obrigatórios
        produtosSalvos.forEach(produto -> {
            assertNotNull(produto.getNome());
            assertNotNull(produto.getPreco());
            assertNotNull(produto.getCategoria());
            assertNotNull(produto.getRestauranteId());
            assertNotNull(produto.getDisponivel());
            assertTrue(produto.getDisponivel());
        });
    }
}
