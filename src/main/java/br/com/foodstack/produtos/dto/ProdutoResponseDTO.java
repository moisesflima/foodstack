package br.com.foodstack.produtos.dto;

import br.com.foodstack.produtos.enums.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para saída de dados de um Produto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponseDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Identificador único do produto.
     */
    private Long id;

    /**
     * Nome do produto.
     */
    private String nome;

    /**
     * Descrição do produto.
     */
    private String descricao;

    /**
     * Preço do produto.
     */
    private BigDecimal preco;

    /**
     * Categoria do produto.
     */
    private Categoria categoria;

    /**
     * ID do restaurante ao qual o produto pertence.
     */
    private Long restauranteId;

    /**
     * URL da imagem do produto.
     */
    private String imagemUrl;

    /**
     * Indica se o produto está disponível.
     */
    private Boolean disponivel;

    /**
     * Tempo estimado de preparo em minutos.
     */
    private Integer tempoPreparoMinutos;

    /**
     * Data de criação do registro.
     */
    private LocalDateTime dataCriacao;

    /**
     * Data da última atualização do registro.
     */
    private LocalDateTime dataAtualizacao;
}
