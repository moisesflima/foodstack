package br.com.foodstack.produtos.dto;

import br.com.foodstack.produtos.enums.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para saída de dados de um Produto.
 */
@Schema(description = "Dados de resposta de um produto")
public record ProdutoResponseDTO(
        @Schema(description = "ID único do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Hambúrguer Gourmet")
        String nome,

        @Schema(description = "Descrição detalhada do produto", example = "Pão brioche, carne 180g, queijo cheddar e bacon")
        String descricao,

        @Schema(description = "Preço do produto em reais", example = "35.00")
        BigDecimal preco,

        @Schema(description = "Categoria do produto", example = "LANCHE")
        Categoria categoria,

        @Schema(description = "ID do restaurante proprietário do produto", example = "1")
        Long restauranteId,

        @Schema(description = "URL da imagem do produto", example = "https://exemplo.com/imagem.jpg")
        String imagemUrl,

        @Schema(description = "Indica se o produto está disponível para venda", example = "true")
        Boolean disponivel,

        @Schema(description = "Tempo estimado de preparo em minutos", example = "20")
        Integer tempoPreparoMinutos,

        @Schema(description = "Data e hora de criação do produto", example = "2024-01-15T10:30:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Data e hora da última atualização do produto", example = "2024-01-15T14:45:00")
        LocalDateTime dataAtualizacao
) implements Serializable {
}
