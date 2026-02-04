package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import org.springframework.data.domain.Pageable;

/**
 * Interface que define os contratos de negócio para gerenciamento de produtos.
 */
public interface ProdutoService {

    /**
     * Lista todos os produtos com paginação.
     *
     * @param pageable Configurações de paginação.
     * @return Página customizada de DTOs de resposta de produtos.
     */
    PageResponseDTO<ProdutoResponseDTO> listarTodos(Pageable pageable);

    /**
     * Busca um produto por ID. Resultado é cacheado.
     *
     * @param id ID do produto.
     * @return DTO de resposta do produto.
     * @throws br.com.foodstack.produtos.exception.ProdutoNotFoundException se o produto não for encontrado.
     */
    ProdutoResponseDTO buscarPorId(Long id);

    /**
     * Busca produtos por categoria com paginação. Resultado é cacheado.
     *
     * @param categoria Categoria dos produtos.
     * @param pageable Configurações de paginação.
     * @return Página customizada de DTOs de resposta de produtos.
     */
    PageResponseDTO<ProdutoResponseDTO> buscarPorCategoria(Categoria categoria, Pageable pageable);

    /**
     * Busca produtos por restaurante com paginação. Resultado é cacheado.
     *
     * @param restauranteId ID do restaurante.
     * @param pageable Configurações de paginação.
     * @return Página customizada de DTOs de resposta de produtos.
     */
    PageResponseDTO<ProdutoResponseDTO> buscarPorRestaurante(Long restauranteId, Pageable pageable);

    /**
     * Cria um novo produto. Invalida caches de listagem.
     *
     * @param requestDTO Dados do novo produto.
     * @return DTO de resposta do produto criado.
     */
    ProdutoResponseDTO criar(ProdutoRequestDTO requestDTO);

    /**
     * Atualiza um produto existente. Atualiza o cache do produto e invalida caches de listagem.
     *
     * @param id ID do produto a ser atualizado.
     * @param requestDTO Novos dados do produto.
     * @return DTO de resposta do produto atualizado.
     * @throws br.com.foodstack.produtos.exception.ProdutoNotFoundException se o produto não for encontrado.
     */
    ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO requestDTO);

    /**
     * Remove um produto. Remove do cache e invalida caches de listagem.
     *
     * @param id ID do produto a ser removido.
     * @throws br.com.foodstack.produtos.exception.ProdutoNotFoundException se o produto não for encontrado.
     */
    void deletar(Long id);
}
