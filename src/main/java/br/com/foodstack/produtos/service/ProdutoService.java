package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.exception.ProdutoNotFoundException;
import br.com.foodstack.produtos.repository.ProdutoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Serviço que contém a lógica de negócio para gerenciamento de produtos.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Lista todos os produtos com paginação.
     *
     * @param pageable Configurações de paginação.
     * @return Página customizada de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    public PageResponseDTO<ProdutoResponseDTO> listarTodos(Pageable pageable) {
        Page<Produto> page = produtoRepository.findAll(pageable);
        return new PageResponseDTO<>(
                page.getContent().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toList()),
                page.getTotalPages(),
                page.getTotalElements()
        );
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
     * @return Página customizada de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtosPorCategoria", key = "#categoria")
    public PageResponseDTO<ProdutoResponseDTO> buscarPorCategoria(Categoria categoria, Pageable pageable) {
        Page<Produto> page = produtoRepository.findByCategoria(categoria, pageable);
        return new PageResponseDTO<>(
                page.getContent().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toList()),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    /**
     * Busca produtos por restaurante com paginação. Resultado é cacheado.
     *
     * @param restauranteId ID do restaurante.
     * @param pageable Configurações de paginação.
     * @return Página customizada de DTOs de resposta de produtos.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtosPorRestaurante", key = "#restauranteId")
    public PageResponseDTO<ProdutoResponseDTO> buscarPorRestaurante(Long restauranteId, Pageable pageable) {
        Page<Produto> page = produtoRepository.findByRestauranteId(restauranteId, pageable);
        return new PageResponseDTO<>(
                page.getContent().stream()
                        .map(this::toResponseDTO)
                        .collect(Collectors.toList()),
                page.getTotalPages(),
                page.getTotalElements()
        );
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
                .nome(requestDTO.nome())
                .descricao(requestDTO.descricao())
                .preco(requestDTO.preco())
                .categoria(requestDTO.categoria())
                .restauranteId(requestDTO.restauranteId())
                .imagemUrl(requestDTO.imagemUrl())
                .disponivel(requestDTO.disponivel() != null ? requestDTO.disponivel() : true)
                .tempoPreparoMinutos(requestDTO.tempoPreparoMinutos())
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

        produto.setNome(requestDTO.nome());
        produto.setDescricao(requestDTO.descricao());
        produto.setPreco(requestDTO.preco());
        produto.setCategoria(requestDTO.categoria());
        produto.setRestauranteId(requestDTO.restauranteId());
        produto.setImagemUrl(requestDTO.imagemUrl());
        if (requestDTO.disponivel() != null) {
            produto.setDisponivel(requestDTO.disponivel());
        }
        produto.setTempoPreparoMinutos(requestDTO.tempoPreparoMinutos());

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
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.getRestauranteId(),
                produto.getImagemUrl(),
                produto.getDisponivel(),
                produto.getTempoPreparoMinutos(),
                produto.getDataCriacao(),
                produto.getDataAtualizacao()
        );
    }
}
