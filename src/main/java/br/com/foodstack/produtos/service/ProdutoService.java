package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.exception.ProdutoNotFoundException;
import br.com.foodstack.produtos.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço que contém a lógica de negócio para gerenciamento de produtos.
 */
@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    /**
     * Lista todos os produtos com paginação.
     *
     * @param pageable Configurações de paginação.
     * @return Página de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listarTodos(Pageable pageable) {
        return produtoRepository.findAll(pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca um produto por ID. Resultado é cacheado.
     *
     * @param id ID do produto.
     * @return DTO de resposta do produto.
     * @throws ProdutoNotFoundException se o produto não for encontrado.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "#id")
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));
        return toResponseDTO(produto);
    }

    /**
     * Busca produtos por categoria com paginação. Resultado é cacheado.
     *
     * @param categoria Categoria dos produtos.
     * @param pageable Configurações de paginação.
     * @return Página de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtosPorCategoria", key = "#categoria")
    public Page<ProdutoResponseDTO> buscarPorCategoria(Categoria categoria, Pageable pageable) {
        return produtoRepository.findByCategoria(categoria, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Busca produtos por restaurante com paginação. Resultado é cacheado.
     *
     * @param restauranteId ID do restaurante.
     * @param pageable Configurações de paginação.
     * @return Página de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtosPorRestaurante", key = "#restauranteId")
    public Page<ProdutoResponseDTO> buscarPorRestaurante(Long restauranteId, Pageable pageable) {
        return produtoRepository.findByRestauranteId(restauranteId, pageable)
                .map(this::toResponseDTO);
    }

    /**
     * Cria um novo produto. Invalida caches de listagem.
     *
     * @param requestDTO Dados do novo produto.
     * @return DTO de resposta do produto criado.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "produtosPorCategoria", allEntries = true),
        @CacheEvict(value = "produtosPorRestaurante", allEntries = true)
    })
    public ProdutoResponseDTO criar(ProdutoRequestDTO requestDTO) {
        Produto produto = Produto.builder()
                .nome(requestDTO.getNome())
                .descricao(requestDTO.getDescricao())
                .preco(requestDTO.getPreco())
                .categoria(requestDTO.getCategoria())
                .restauranteId(requestDTO.getRestauranteId())
                .imagemUrl(requestDTO.getImagemUrl())
                .disponivel(requestDTO.getDisponivel() != null ? requestDTO.getDisponivel() : true)
                .tempoPreparoMinutos(requestDTO.getTempoPreparoMinutos())
                .build();

        Produto salvo = produtoRepository.save(produto);
        return toResponseDTO(salvo);
    }

    /**
     * Atualiza um produto existente. Atualiza o cache do produto e invalida caches de listagem.
     *
     * @param id ID do produto a ser atualizado.
     * @param requestDTO Novos dados do produto.
     * @return DTO de resposta do produto atualizado.
     * @throws ProdutoNotFoundException se o produto não for encontrado.
     */
    @Transactional
    @Caching(
        put = @CachePut(value = "produtos", key = "#id"),
        evict = {
            @CacheEvict(value = "produtosPorCategoria", allEntries = true),
            @CacheEvict(value = "produtosPorRestaurante", allEntries = true)
        }
    )
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO requestDTO) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNotFoundException(id));

        produto.setNome(requestDTO.getNome());
        produto.setDescricao(requestDTO.getDescricao());
        produto.setPreco(requestDTO.getPreco());
        produto.setCategoria(requestDTO.getCategoria());
        produto.setRestauranteId(requestDTO.getRestauranteId());
        produto.setImagemUrl(requestDTO.getImagemUrl());
        if (requestDTO.getDisponivel() != null) {
            produto.setDisponivel(requestDTO.getDisponivel());
        }
        produto.setTempoPreparoMinutos(requestDTO.getTempoPreparoMinutos());

        Produto atualizado = produtoRepository.save(produto);
        return toResponseDTO(atualizado);
    }

    /**
     * Remove um produto. Remove do cache e invalida caches de listagem.
     *
     * @param id ID do produto a ser removido.
     * @throws ProdutoNotFoundException se o produto não for encontrado.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "produtos", key = "#id"),
        @CacheEvict(value = "produtosPorCategoria", allEntries = true),
        @CacheEvict(value = "produtosPorRestaurante", allEntries = true)
    })
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new ProdutoNotFoundException(id);
        }
        produtoRepository.deleteById(id);
    }

    /**
     * Converte uma entidade Produto para ProdutoResponseDTO.
     *
     * @param produto Entidade Produto.
     * @return DTO de resposta correspondente.
     */
    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return ProdutoResponseDTO.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .categoria(produto.getCategoria())
                .restauranteId(produto.getRestauranteId())
                .imagemUrl(produto.getImagemUrl())
                .disponivel(produto.getDisponivel())
                .tempoPreparoMinutos(produto.getTempoPreparoMinutos())
                .dataCriacao(produto.getDataCriacao())
                .dataAtualizacao(produto.getDataAtualizacao())
                .build();
    }
}
