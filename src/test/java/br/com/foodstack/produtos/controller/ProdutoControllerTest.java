package br.com.foodstack.produtos.controller;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.exception.ProdutoNotFoundException;
import br.com.foodstack.produtos.service.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@DisplayName("ProdutoController - Testes Unitários")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProdutoService produtoService;

    private ProdutoResponseDTO responseDTO;
    private ProdutoRequestDTO requestDTO;
    private PageResponseDTO<ProdutoResponseDTO> pageResponseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ProdutoResponseDTO(
                1L,
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                "http://exemplo.com/imagem.jpg",
                true,
                20,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        requestDTO = new ProdutoRequestDTO(
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                "http://exemplo.com/imagem.jpg",
                true,
                20
        );

        pageResponseDTO = new PageResponseDTO<>(
                List.of(responseDTO),
                1,
                1L
        );
    }

    @Test
    @DisplayName("GET /api/produtos - Deve listar todos os produtos com paginação")
    void deveListarTodosProdutosComPaginacao() throws Exception {
        // Arrange
        when(produtoService.listarTodos(any())).thenReturn(pageResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/produtos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("Hambúrguer Gourmet"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(produtoService, times(1)).listarTodos(any());
    }

    @Test
    @DisplayName("GET /api/produtos - Deve usar valores padrão de paginação")
    void deveUsarValoresPadraoDePaginacao() throws Exception {
        // Arrange
        when(produtoService.listarTodos(PageRequest.of(0, 10))).thenReturn(pageResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk());

        verify(produtoService, times(1)).listarTodos(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("GET /api/produtos/{id} - Deve buscar produto por ID")
    void deveBuscarProdutoPorId() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(1L)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Hambúrguer Gourmet"))
                .andExpect(jsonPath("$.preco").value(35.00));

        verify(produtoService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        // Arrange
        when(produtoService.buscarPorId(999L)).thenThrow(new ProdutoNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(get("/api/produtos/999"))
                .andExpect(status().isNotFound());

        verify(produtoService, times(1)).buscarPorId(999L);
    }

    @Test
    @DisplayName("GET /api/produtos/categoria/{categoria} - Deve buscar por categoria")
    void deveBuscarProdutosPorCategoria() throws Exception {
        // Arrange
        when(produtoService.buscarPorCategoria(eq(Categoria.LANCHE), any())).thenReturn(pageResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/categoria/LANCHE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].categoria").value("LANCHE"));

        verify(produtoService, times(1)).buscarPorCategoria(eq(Categoria.LANCHE), any());
    }

    @Test
    @DisplayName("GET /api/produtos/restaurante/{restauranteId} - Deve buscar por restaurante")
    void deveBuscarProdutosPorRestaurante() throws Exception {
        // Arrange
        when(produtoService.buscarPorRestaurante(eq(1L), any())).thenReturn(pageResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/produtos/restaurante/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].restauranteId").value(1));

        verify(produtoService, times(1)).buscarPorRestaurante(eq(1L), any());
    }

    @Test
    @DisplayName("POST /api/produtos - Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        // Arrange
        when(produtoService.criar(any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Hambúrguer Gourmet"))
                .andExpect(jsonPath("$.preco").value(35.00));

        verify(produtoService, times(1)).criar(any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar 400 quando dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "",  // nome vazio - inválido
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any());
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar 400 quando preço negativo")
    void deveRetornar400QuandoPrecoNegativo() throws Exception {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "Produto",
                "Descrição",
                new BigDecimal("-10.00"),  // preço negativo - inválido
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any());
    }

    @Test
    @DisplayName("POST /api/produtos - Deve retornar 400 quando restauranteId null")
    void deveRetornar400QuandoRestauranteIdNull() throws Exception {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "Produto",
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                null,  // restauranteId null - inválido
                null,
                true,
                20
        );

        // Act & Assert
        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).criar(any());
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        // Arrange
        when(produtoService.atualizar(eq(1L), any(ProdutoRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Hambúrguer Gourmet"));

        verify(produtoService, times(1)).atualizar(eq(1L), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404AoAtualizarProdutoInexistente() throws Exception {
        // Arrange
        when(produtoService.atualizar(eq(999L), any(ProdutoRequestDTO.class)))
                .thenThrow(new ProdutoNotFoundException(999L));

        // Act & Assert
        mockMvc.perform(put("/api/produtos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(produtoService, times(1)).atualizar(eq(999L), any(ProdutoRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} - Deve retornar 400 quando dados inválidos")
    void deveRetornar400AoAtualizarComDadosInvalidos() throws Exception {
        // Arrange
        ProdutoRequestDTO requestInvalido = new ProdutoRequestDTO(
                "",  // nome vazio - inválido
                "Descrição",
                new BigDecimal("35.00"),
                Categoria.LANCHE,
                1L,
                null,
                true,
                20
        );

        // Act & Assert
        mockMvc.perform(put("/api/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(produtoService, never()).atualizar(anyLong(), any());
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() throws Exception {
        // Arrange
        doNothing().when(produtoService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/produtos/1"))
                .andExpect(status().isNoContent());

        verify(produtoService, times(1)).deletar(1L);
    }

    @Test
    @DisplayName("DELETE /api/produtos/{id} - Deve retornar 404 quando produto não existe")
    void deveRetornar404AoDeletarProdutoInexistente() throws Exception {
        // Arrange
        doThrow(new ProdutoNotFoundException(999L)).when(produtoService).deletar(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/produtos/999"))
                .andExpect(status().isNotFound());

        verify(produtoService, times(1)).deletar(999L);
    }
}
