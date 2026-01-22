package br.com.foodstack.produtos.repository;

import br.com.foodstack.produtos.entity.Produto;
import br.com.foodstack.produtos.enums.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Produto.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Busca produtos por categoria com paginação.
     *
     * @param categoria Categoria do produto.
     * @param pageable Configurações de paginação.
     * @return Página de produtos encontrados.
     */
    Page<Produto> findByCategoria(Categoria categoria, Pageable pageable);

    /**
     * Busca produtos por ID do restaurante com paginação.
     *
     * @param restauranteId ID do restaurante.
     * @param pageable Configurações de paginação.
     * @return Página de produtos encontrados.
     */
    Page<Produto> findByRestauranteId(Long restauranteId, Pageable pageable);
    
    /**
     * Busca todos os produtos de uma categoria.
     * @param categoria Categoria do produto.
     * @return Lista de produtos.
     */
    List<Produto> findByCategoria(Categoria categoria);

    /**
     * Busca todos os produtos de um restaurante.
     * @param restauranteId ID do restaurante.
     * @return Lista de produtos.
     */
    List<Produto> findByRestauranteId(Long restauranteId);
}
