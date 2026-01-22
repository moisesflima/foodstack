package br.com.foodstack.produtos.controller;

import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller que expõe os endpoints da API de Produtos.
 */
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    /**
     * Lista todos os produtos com paginação.
     * GET /api/produtos
     *
     * @param pageable Configurações de paginação.
     * @return Página de produtos.
     */
    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(produtoService.listarTodos(pageable));
    }

    /**
     * Busca um produto por ID.
     * GET /api/produtos/{id}
     *
     * @param id ID do produto.
     * @return Produto encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    /**
     * Busca produtos por categoria com paginação.
     * GET /api/produtos/categoria/{categoria}
     *
     * @param categoria Categoria do produto.
     * @param pageable Configurações de paginação.
     * @return Página de produtos da categoria.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable Categoria categoria, Pageable pageable) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoria, pageable));
    }

    /**
     * Busca produtos por restaurante com paginação.
     * GET /api/produtos/restaurante/{restauranteId}
     *
     * @param restauranteId ID do restaurante.
     * @param pageable Configurações de paginação.
     * @return Página de produtos do restaurante.
     */
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<Page<ProdutoResponseDTO>> buscarPorRestaurante(@PathVariable Long restauranteId, Pageable pageable) {
        return ResponseEntity.ok(produtoService.buscarPorRestaurante(restauranteId, pageable));
    }

    /**
     * Cria um novo produto.
     * POST /api/produtos
     *
     * @param requestDTO Dados do novo produto.
     * @return Produto criado.
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@RequestBody @Valid ProdutoRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(requestDTO));
    }

    /**
     * Atualiza um produto existente.
     * PUT /api/produtos/{id}
     *
     * @param id ID do produto a ser atualizado.
     * @param requestDTO Novos dados do produto.
     * @return Produto atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO requestDTO) {
        return ResponseEntity.ok(produtoService.atualizar(id, requestDTO));
    }

    /**
     * Remove um produto.
     * DELETE /api/produtos/{id}
     *
     * @param id ID do produto a ser removido.
     * @return Resposta vazia (204 No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
