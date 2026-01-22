# API REST de Produtos - Estilo iFood

Esta é uma API REST completa desenvolvida com Spring Boot para o gerenciamento de produtos de um aplicativo de delivery estilo iFood.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.9**
- **Spring Data JPA**
- **H2 Database** (em memória)
- **Maven**
- **Lombok**
- **Spring Cache**
- **Bean Validation**

## Como Executar

1. Certifique-se de ter o Java 17+ e o Maven instalados.
2. Clone o repositório ou baixe o código.
3. No diretório raiz, execute o comando:
   ```bash
   mvn spring-boot:run
   ```
4. A aplicação estará disponível em `http://localhost:8080`.
5. O console do banco de dados H2 pode ser acessado em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:produtosdb`, User: `sa`, Password: vazia).

## Endpoints da API

### Produtos

- `GET /api/produtos` - Lista todos os produtos (paginado)
- `GET /api/produtos/{id}` - Busca um produto por ID (com cache)
- `GET /api/produtos/categoria/{categoria}` - Busca produtos por categoria (paginado, com cache)
- `GET /api/produtos/restaurante/{restauranteId}` - Busca produtos por restaurante (paginado, com cache)
- `POST /api/produtos` - Cria um novo produto
- `PUT /api/produtos/{id}` - Atualiza um produto existente
- `DELETE /api/produtos/{id}` - Remove um produto

### Exemplos de Requisições

#### Criar Produto (POST)
```json
{
  "nome": "Pizza Pepperoni",
  "descricao": "Massa fina, molho de tomate, mussarela e pepperoni",
  "preco": 48.90,
  "categoria": "PIZZA",
  "restauranteId": 1,
  "imagemUrl": "http://exemplo.com/pizza.jpg",
  "disponivel": true,
  "tempoPreparoMinutos": 30
}
```

#### Categorias Disponíveis
- `BEBIDA`
- `ENTRADA`
- `PRATO_PRINCIPAL`
- `SOBREMESA`
- `LANCHE`
- `PIZZA`
- `JAPONESA`

## Boas Práticas Implementadas

- **Arquitetura em Camadas**: Separação clara entre Controller, Service, Repository, Entity e DTO.
- **SOLID**: Princípios aplicados para garantir manutenção e extensibilidade.
- **Cache Local**: Implementado com `@Cacheable`, `@CachePut` e `@CacheEvict` para otimização de performance.
- **Tratamento de Exceções**: `@ControllerAdvice` para capturar e formatar erros de forma padronizada.
- **Validação**: Uso de Bean Validation para garantir a integridade dos dados de entrada.
- **JavaDoc**: Documentação completa em todas as classes e métodos públicos.
