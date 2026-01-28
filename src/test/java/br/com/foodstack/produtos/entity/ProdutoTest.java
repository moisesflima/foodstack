package br.com.foodstack.produtos.entity;

import br.com.foodstack.produtos.enums.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Produto - Testes Unitários")
class ProdutoTest {

    @Test
    @DisplayName("Deve criar produto usando construtor padrão")
    void deveCriarProdutoUsandoConstrutorPadrao() {
        // Act
        Produto produto = new Produto();

        // Assert
        assertNotNull(produto);
        assertNull(produto.getId());
        assertNull(produto.getNome());
    }

    @Test
    @DisplayName("Deve criar produto usando Builder com todos os campos")
    void deveCriarProdutoUsandoBuilderComTodosCampos() {
        // Act
        Produto produto = Produto.builder()
                .nome("Hambúrguer Gourmet")
                .descricao("Pão brioche, carne 180g")
                .preco(new BigDecimal("35.00"))
                .categoria(Categoria.LANCHE)
                .restauranteId(1L)
                .imagemUrl("http://exemplo.com/imagem.jpg")
                .disponivel(true)
                .tempoPreparoMinutos(20)
                .build();

        // Assert
        assertNotNull(produto);
        assertEquals("Hambúrguer Gourmet", produto.getNome());
        assertEquals("Pão brioche, carne 180g", produto.getDescricao());
        assertEquals(new BigDecimal("35.00"), produto.getPreco());
        assertEquals(Categoria.LANCHE, produto.getCategoria());
        assertEquals(1L, produto.getRestauranteId());
        assertEquals("http://exemplo.com/imagem.jpg", produto.getImagemUrl());
        assertTrue(produto.getDisponivel());
        assertEquals(20, produto.getTempoPreparoMinutos());
    }

    @Test
    @DisplayName("Deve criar produto com disponivel default true")
    void deveCriarProdutoComDisponivelDefaultTrue() {
        // Act
        Produto produto = Produto.builder()
                .nome("Pizza")
                .preco(new BigDecimal("45.00"))
                .categoria(Categoria.PIZZA)
                .restauranteId(2L)
                .build();

        // Assert
        assertNotNull(produto);
        assertTrue(produto.getDisponivel());
    }

    @Test
    @DisplayName("Deve permitir alterar disponivel para false")
    void devePermitirAlterarDisponivelParaFalse() {
        // Act
        Produto produto = Produto.builder()
                .nome("Pizza")
                .preco(new BigDecimal("45.00"))
                .categoria(Categoria.PIZZA)
                .restauranteId(2L)
                .disponivel(false)
                .build();

        // Assert
        assertNotNull(produto);
        assertFalse(produto.getDisponivel());
    }

    @Test
    @DisplayName("Deve permitir usar setters para modificar produto")
    void devePermitirUsarSettersParaModificarProduto() {
        // Arrange
        Produto produto = new Produto();

        // Act
        produto.setId(1L);
        produto.setNome("Sushi");
        produto.setDescricao("Combo variado");
        produto.setPreco(new BigDecimal("80.00"));
        produto.setCategoria(Categoria.JAPONESA);
        produto.setRestauranteId(3L);
        produto.setImagemUrl("http://exemplo.com/sushi.jpg");
        produto.setDisponivel(false);
        produto.setTempoPreparoMinutos(40);
        
        LocalDateTime agora = LocalDateTime.now();
        produto.setDataCriacao(agora);
        produto.setDataAtualizacao(agora);

        // Assert
        assertEquals(1L, produto.getId());
        assertEquals("Sushi", produto.getNome());
        assertEquals("Combo variado", produto.getDescricao());
        assertEquals(new BigDecimal("80.00"), produto.getPreco());
        assertEquals(Categoria.JAPONESA, produto.getCategoria());
        assertEquals(3L, produto.getRestauranteId());
        assertEquals("http://exemplo.com/sushi.jpg", produto.getImagemUrl());
        assertFalse(produto.getDisponivel());
        assertEquals(40, produto.getTempoPreparoMinutos());
        assertEquals(agora, produto.getDataCriacao());
        assertEquals(agora, produto.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve permitir criar produto com campos opcionais nulos")
    void devePermitirCriarProdutoComCamposOpcionaisNulos() {
        // Act
        Produto produto = Produto.builder()
                .nome("Produto Simples")
                .preco(new BigDecimal("10.00"))
                .restauranteId(1L)
                .build();

        // Assert
        assertNotNull(produto);
        assertEquals("Produto Simples", produto.getNome());
        assertNull(produto.getDescricao());
        assertNull(produto.getCategoria());
        assertNull(produto.getImagemUrl());
        assertNull(produto.getTempoPreparoMinutos());
        assertTrue(produto.getDisponivel()); // default true
    }

    @Test
    @DisplayName("Deve permitir encadear métodos do Builder")
    void devePermitirEncadearMetodosDoBuilder() {
        // Act
        Produto produto = Produto.builder()
                .nome("Bebida")
                .preco(new BigDecimal("6.50"))
                .categoria(Categoria.BEBIDA)
                .restauranteId(1L)
                .disponivel(true)
                .tempoPreparoMinutos(5)
                .build();

        // Assert
        assertNotNull(produto);
        assertEquals("Bebida", produto.getNome());
        assertEquals(new BigDecimal("6.50"), produto.getPreco());
        assertEquals(Categoria.BEBIDA, produto.getCategoria());
    }

    @Test
    @DisplayName("Deve permitir modificar produto após criação")
    void devePermitirModificarProdutoAposCriacao() {
        // Arrange
        Produto produto = Produto.builder()
                .nome("Nome Original")
                .preco(new BigDecimal("10.00"))
                .restauranteId(1L)
                .build();

        // Act
        produto.setNome("Nome Modificado");
        produto.setPreco(new BigDecimal("15.00"));
        produto.setDisponivel(false);

        // Assert
        assertEquals("Nome Modificado", produto.getNome());
        assertEquals(new BigDecimal("15.00"), produto.getPreco());
        assertFalse(produto.getDisponivel());
    }
}
