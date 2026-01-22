package br.com.foodstack.produtos.entity;

import br.com.foodstack.produtos.enums.Categoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Produto no banco de dados.
 */
@Entity
@Table(name = "produtos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    /**
     * Identificador único do produto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Descrição detalhada do produto.
     */
    private String descricao;

    /**
     * Preço do produto.
     */
    @Column(nullable = false)
    private BigDecimal preco;

    /**
     * Categoria do produto.
     */
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    /**
     * ID do restaurante ao qual o produto pertence.
     */
    @Column(nullable = false)
    private Long restauranteId;

    /**
     * URL da imagem do produto.
     */
    private String imagemUrl;

    /**
     * Indica se o produto está disponível para venda.
     */
    @Builder.Default
    private Boolean disponivel = true;

    /**
     * Tempo estimado de preparo em minutos.
     */
    private Integer tempoPreparoMinutos;

    /**
     * Data e hora de criação do registro.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data e hora da última atualização do registro.
     */
    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
}
