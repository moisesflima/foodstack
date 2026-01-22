package br.com.foodstack.produtos.config;

import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

/**
 * Classe responsável por popular o banco de dados com dados iniciais para teste.
 */
@Configuration
@RequiredArgsConstructor
public class DadosIniciaisConfig implements CommandLineRunner {

    private final ProdutoRepository produtoRepository;

    @Override
    public void run(String... args) {
        if (produtoRepository.count() == 0) {
            List<Produto> produtos = List.of(
                    Produto.builder()
                            .nome("Coca-Cola 350ml")
                            .descricao("Lata de refrigerante gelada")
                            .preco(new BigDecimal("6.50"))
                            .categoria(Categoria.BEBIDA)
                            .restauranteId(1L)
                            .disponivel(true)
                            .tempoPreparoMinutos(5)
                            .build(),
                    Produto.builder()
                            .nome("Suco de Laranja 500ml")
                            .descricao("Suco natural de laranja")
                            .preco(new BigDecimal("12.00"))
                            .categoria(Categoria.BEBIDA)
                            .restauranteId(1L)
                            .disponivel(true)
                            .tempoPreparoMinutos(10)
                            .build(),
                    Produto.builder()
                            .nome("Batata Frita G")
                            .descricao("Porção grande de batatas crocantes")
                            .preco(new BigDecimal("25.00"))
                            .categoria(Categoria.ENTRADA)
                            .restauranteId(2L)
                            .disponivel(true)
                            .tempoPreparoMinutos(15)
                            .build(),
                    Produto.builder()
                            .nome("Hambúrguer Gourmet")
                            .descricao("Pão brioche, carne 180g, queijo cheddar e bacon")
                            .preco(new BigDecimal("35.00"))
                            .categoria(Categoria.LANCHE)
                            .restauranteId(2L)
                            .disponivel(true)
                            .tempoPreparoMinutos(20)
                            .build(),
                    Produto.builder()
                            .nome("Pizza Calabresa")
                            .descricao("Molho de tomate, mussarela, calabresa e cebola")
                            .preco(new BigDecimal("45.00"))
                            .categoria(Categoria.PIZZA)
                            .restauranteId(3L)
                            .disponivel(true)
                            .tempoPreparoMinutos(30)
                            .build(),
                    Produto.builder()
                            .nome("Pizza Margherita")
                            .descricao("Molho de tomate, mussarela, manjericão e azeite")
                            .preco(new BigDecimal("42.00"))
                            .categoria(Categoria.PIZZA)
                            .restauranteId(3L)
                            .disponivel(true)
                            .tempoPreparoMinutos(25)
                            .build(),
                    Produto.builder()
                            .nome("Combo Sushi 20 peças")
                            .descricao("Variedade de sushis e sashimis")
                            .preco(new BigDecimal("80.00"))
                            .categoria(Categoria.JAPONESA)
                            .restauranteId(4L)
                            .disponivel(true)
                            .tempoPreparoMinutos(40)
                            .build(),
                    Produto.builder()
                            .nome("Petit Gâteau")
                            .descricao("Bolinho quente de chocolate com sorvete de baunilha")
                            .preco(new BigDecimal("22.00"))
                            .categoria(Categoria.SOBREMESA)
                            .restauranteId(1L)
                            .disponivel(true)
                            .tempoPreparoMinutos(15)
                            .build(),
                    Produto.builder()
                            .nome("Espaguete à Bolonhesa")
                            .descricao("Massa artesanal com molho de carne moída")
                            .preco(new BigDecimal("38.00"))
                            .categoria(Categoria.PRATO_PRINCIPAL)
                            .restauranteId(5L)
                            .disponivel(true)
                            .tempoPreparoMinutos(25)
                            .build(),
                    Produto.builder()
                            .nome("Bruschetta de Tomate")
                            .descricao("Pão italiano tostado com tomates e manjericão")
                            .preco(new BigDecimal("18.00"))
                            .categoria(Categoria.ENTRADA)
                            .restauranteId(5L)
                            .disponivel(true)
                            .tempoPreparoMinutos(12)
                            .build()
            );

            produtoRepository.saveAll(produtos);
        }
    }
}
