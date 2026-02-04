package br.com.foodstack.produtos.service;

import br.com.foodstack.produtos.dto.PageResponseDTO;
import br.com.foodstack.produtos.dto.ProdutoRequestDTO;
import br.com.foodstack.produtos.dto.ProdutoResponseDTO;
import br.com.foodstack.produtos.enums.Categoria;
import br.com.foodstack.produtos.service.impl.ProdutoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("ProdutoService - Testes de Contrato")
class ProdutoServiceInterfaceTest {

    @Autowired
    private ProdutoServiceImpl produtoService;

    private Pageable pageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("ProdutoServiceImpl deve implementar ProdutoService")
    void testProdutoServiceImplementsInterface() {
        assertNotNull(produtoService);
        assertTrue(produtoService instanceof ProdutoService,
                "ProdutoServiceImpl deve implementar ProdutoService");
    }

    @Test
    @DisplayName("Deve ter método listarTodos implementado")
    void testListarTodosMethodExists() {
        // When
        PageResponseDTO<ProdutoResponseDTO> result = produtoService.listarTodos(pageable);

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve ter método buscarPorId implementado")
    void testBuscarPorIdMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "buscarPorId", Long.class),
                "Método buscarPorId deve estar implementado");
    }

    @Test
    @DisplayName("Deve ter método buscarPorCategoria implementado")
    void testBuscarPorCategoriaMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "buscarPorCategoria", Categoria.class, Pageable.class),
                "Método buscarPorCategoria deve estar implementado");
    }

    @Test
    @DisplayName("Deve ter método buscarPorRestaurante implementado")
    void testBuscarPorRestauranteMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "buscarPorRestaurante", Long.class, Pageable.class),
                "Método buscarPorRestaurante deve estar implementado");
    }

    @Test
    @DisplayName("Deve ter método criar implementado")
    void testCriarMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "criar", ProdutoRequestDTO.class),
                "Método criar deve estar implementado");
    }

    @Test
    @DisplayName("Deve ter método atualizar implementado")
    void testAtualizarMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "atualizar", Long.class, ProdutoRequestDTO.class),
                "Método atualizar deve estar implementado");
    }

    @Test
    @DisplayName("Deve ter método deletar implementado")
    void testDeletarMethodExists() {
        // Then
        assertTrue(hasMethod(produtoService, "deletar", Long.class),
                "Método deletar deve estar implementado");
    }

    @Test
    @DisplayName("Todos os métodos da interface devem estar implementados")
    void testAllInterfaceMethodsImplemented() {
        // When
        Class<?>[] interfaceInterfaces = ProdutoServiceImpl.class.getInterfaces();

        // Then
        assertTrue(java.util.Arrays.asList(interfaceInterfaces).contains(ProdutoService.class),
                "ProdutoServiceImpl deve implementar ProdutoService");
    }

    private boolean hasMethod(Object object, String methodName, Class<?>... parameterTypes) {
        try {
            object.getClass().getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
