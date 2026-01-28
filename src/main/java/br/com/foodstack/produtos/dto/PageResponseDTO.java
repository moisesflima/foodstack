package br.com.foodstack.produtos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * DTO customizado para resposta paginada.
 * Contém apenas os campos essenciais: conteúdo, total de páginas e total de elementos.
 */
@Schema(description = "Resposta paginada customizada")
public record PageResponseDTO<T>(
        @Schema(description = "Lista de elementos da página atual")
        List<T> content,

        @Schema(description = "Número total de páginas disponíveis", example = "4")
        int totalPages,

        @Schema(description = "Número total de elementos no banco de dados", example = "10")
        long totalElements
) implements Serializable {
}
