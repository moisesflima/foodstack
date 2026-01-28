package br.com.foodstack.produtos.dto;

import br.com.foodstack.produtos.enums.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para entrada de dados na criação ou atualização de um Produto.
 */
@Schema(description = "Dados para criação ou atualização de um produto")
public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto", example = "Hambúrguer Gourmet", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @Schema(description = "Descrição detalhada do produto", example = "Pão brioche, carne 180g, queijo cheddar e bacon")
        String descricao,

        @Schema(description = "Preço do produto em reais", example = "35.00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser um valor positivo")
        BigDecimal preco,

        @Schema(description = "Categoria do produto", example = "LANCHE")
        Categoria categoria,

        @Schema(description = "ID do restaurante proprietário do produto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O ID do restaurante é obrigatório")
        Long restauranteId,

        @Schema(description = "URL da imagem do produto", example = "https://exemplo.com/imagem.jpg")
        String imagemUrl,

        @Schema(description = "Indica se o produto está disponível para venda", example = "true", defaultValue = "true")
        Boolean disponivel,

        @Schema(description = "Tempo estimado de preparo em minutos", example = "20")
        Integer tempoPreparoMinutos
) {
}
