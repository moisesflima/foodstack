package br.com.foodstack.produtos.controller;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller que expõe os endpoints da API de Produtos.
 */
@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Lista todos os produtos com paginação.
     * GET /api/produtos?page=0&size=10
     *
     * @param page Número da página (padrão: 0).
     * @param size Tamanho da página (padrão: 10).
     * @return Página customizada de produtos.
     */
    @Operation(
            summary = "Listar todos os produtos",
            description = "Retorna uma lista paginada de todos os produtos cadastrados no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> listarTodos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(produtoService.listarTodos(pageable));
    }

    /**
     * Busca um produto por ID.
     * GET /api/produtos/{id}
     *
     * @param id ID do produto.
     * @return Produto encontrado.
     */
    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna os detalhes de um produto específico pelo seu identificador único. O resultado é cacheado no Redis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    /**
     * Busca produtos por categoria com paginação.
     * GET /api/produtos/categoria/{categoria}?page=0&size=10
     *
     * @param categoria Categoria do produto.
     * @param page Número da página (padrão: 0).
     * @param size Tamanho da página (padrão: 10).
     * @return Página customizada de produtos da categoria.
     */
    @Operation(
            summary = "Buscar produtos por categoria",
            description = "Retorna uma lista paginada de produtos filtrados por categoria. O resultado é cacheado no Redis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos da categoria retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Categoria inválida",
                    content = @Content)
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Categoria do produto (BEBIDA, ENTRADA, PRATO_PRINCIPAL, SOBREMESA, LANCHE, PIZZA, JAPONESA)", 
                    example = "LANCHE", required = true)
            @PathVariable Categoria categoria,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoria, pageable));
    }

    /**
     * Busca produtos por restaurante com paginação.
     * GET /api/produtos/restaurante/{restauranteId}?page=0&size=10
     *
     * @param restauranteId ID do restaurante.
     * @param page Número da página (padrão: 0).
     * @param size Tamanho da página (padrão: 10).
     * @return Página customizada de produtos do restaurante.
     */
    @Operation(
            summary = "Buscar produtos por restaurante",
            description = "Retorna uma lista paginada de produtos de um restaurante específico. O resultado é cacheado no Redis."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos do restaurante retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class)))
    })
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante", example = "1", required = true)
            @PathVariable Long restauranteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(produtoService.buscarPorRestaurante(restauranteId, pageable));
    }

    /**
     * Cria um novo produto.
     * POST /api/produtos
     *
     * @param requestDTO Dados do novo produto.
     * @return Produto criado.
     */
    @Operation(
            summary = "Criar novo produto",
            description = "Cria um novo produto no sistema. Invalida os caches de listagem por categoria e restaurante."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do produto a ser criado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProdutoRequestDTO.class)))
            @RequestBody @Valid ProdutoRequestDTO requestDTO) {
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
    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto existente. Atualiza o cache do produto e invalida os caches de listagem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProdutoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto a ser atualizado", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novos dados do produto",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProdutoRequestDTO.class)))
            @RequestBody @Valid ProdutoRequestDTO requestDTO) {
        return ResponseEntity.ok(produtoService.atualizar(id, requestDTO));
    }

    /**
     * Remove um produto.
     * DELETE /api/produtos/{id}
     *
     * @param id ID do produto a ser removido.
     * @return Resposta vazia (204 No Content).
     */
    @Operation(
            summary = "Deletar produto",
            description = "Remove um produto do sistema. Remove do cache e invalida os caches de listagem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do produto a ser removido", example = "1", required = true)
            @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
