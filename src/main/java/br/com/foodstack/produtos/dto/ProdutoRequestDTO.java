package br.com.foodstack.produtos.dto;

import br.com.foodstack.produtos.enums.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para entrada de dados na criação ou atualização de um Produto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequestDTO {

    /**
     * Nome do produto. Obrigatório e não pode ser vazio.
     */
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    /**
     * Descrição do produto.
     */
    private String descricao;

    /**
     * Preço do produto. Obrigatório e deve ser um valor positivo.
     */
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser um valor positivo")
    private BigDecimal preco;

    /**
     * Categoria do produto.
     */
    private Categoria categoria;

    /**
     * ID do restaurante ao qual o produto pertence. Obrigatório.
     */
    @NotNull(message = "O ID do restaurante é obrigatório")
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
}
