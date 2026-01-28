package br.com.foodstack.produtos.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum que representa as categorias de produtos disponíveis no sistema.
 */
@Schema(description = "Categorias de produtos disponíveis no sistema")
public enum Categoria {
    @Schema(description = "Bebidas (refrigerantes, sucos, etc.)")
    BEBIDA,
    
    @Schema(description = "Entradas e aperitivos")
    ENTRADA,
    
    @Schema(description = "Pratos principais")
    PRATO_PRINCIPAL,
    
    @Schema(description = "Sobremesas")
    SOBREMESA,
    
    @Schema(description = "Lanches e sanduíches")
    LANCHE,
    
    @Schema(description = "Pizzas")
    PIZZA,
    
    @Schema(description = "Comida japonesa")
    JAPONESA
}
