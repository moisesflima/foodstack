package br.com.foodstack.produtos.exception;

/**
 * Exceção lançada quando um produto não é encontrado.
 */
public class ProdutoNotFoundException extends RuntimeException {
    public ProdutoNotFoundException(Long id) {
        super("Produto não encontrado com o ID: " + id);
    }
}
