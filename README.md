# API REST de Produtos - FoodStack

Esta é uma API REST completa desenvolvida com Spring Boot para o gerenciamento de produtos de um aplicativo de delivery estilo iFood.

## 📋 Índice

- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Como Executar](#como-executar)
  - [Opção 1: Docker (Recomendado)](#opção-1-docker-recomendado)
  - [Opção 2: Execução Local](#opção-2-execução-local)
- [Documentação da API](#documentação-da-api)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de Requisições](#exemplos-de-requisições)
- [Cache Redis](#cache-redis)
- [Testes](#testes)
- [Boas Práticas Implementadas](#boas-práticas-implementadas)

## 🚀 Tecnologias Utilizadas

- **Java 17** (Records, Pattern Matching)
- **Spring Boot 3.5.9**
- **Spring Data JPA** (Persistência)
- **Spring Cache** (Abstração de cache)
- **Spring Data Redis** (Cache distribuído)
- **H2 Database** (Banco de dados em memória)
- **Redis 3.0.7** (Cache distribuído)
- **SpringDoc OpenAPI 3.0** (Documentação Swagger)
- **Bean Validation** (Validação de dados)
- **Docker & Docker Compose** (Containerização)
- **Maven** (Gerenciamento de dependências)
- **JUnit 5 & Mockito** (Testes unitários - 100% cobertura)

## 🐳 Como Executar

### Opção 1: Docker (Recomendado)

Esta é a forma mais simples e recomendada para executar a aplicação, pois não requer instalação de Java ou Maven localmente.

#### Pré-requisitos
- [Docker](https://www.docker.com/get-started) instalado
- [Docker Compose](https://docs.docker.com/compose/install/) instalado

#### Passos

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd demo
   ```

2. **Suba os containers**
   ```bash
   docker-compose up -d
   ```
   
   Este comando irá:
   - Construir a imagem da aplicação Spring Boot
   - Baixar a imagem do Redis 3.0.7
   - Criar uma rede bridge para comunicação entre containers
   - Iniciar o Redis e aguardar o healthcheck
   - Iniciar a API Spring Boot

3. **Verifique os logs**
   ```bash
   # Logs de todos os serviços
   docker-compose logs -f
   
   # Logs apenas da API
   docker-compose logs -f produtos-api
   
   # Logs apenas do Redis
   docker-compose logs -f redis
   ```

4. **Acesse a aplicação**
   - **API REST**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **OpenAPI JSON**: http://localhost:8080/v3/api-docs
   - **H2 Console**: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:produtosdb`
     - Username: `sa`
     - Password: (deixar em branco)

5. **Parar os containers**
   ```bash
   docker-compose down
   ```

6. **Rebuild (após alterações no código)**
   ```bash
   docker-compose down
   docker-compose build --no-cache
   docker-compose up -d
   ```

#### Comandos Úteis Docker

```bash
# Ver status dos containers
docker-compose ps

# Acessar o shell do container da API
docker-compose exec produtos-api sh

# Acessar o Redis CLI
docker-compose exec redis redis-cli

# Ver chaves no cache Redis
docker-compose exec redis redis-cli KEYS "*"

# Limpar todo o cache Redis
docker-compose exec redis redis-cli FLUSHALL

# Ver logs em tempo real
docker-compose logs -f

# Reiniciar apenas a API
docker-compose restart produtos-api

# Remover containers, volumes e redes
docker-compose down -v
```

### Opção 2: Execução Local

Se preferir executar a aplicação localmente sem Docker:

#### Pré-requisitos
- Java 17+ instalado
- Maven 3.6+ instalado
- Redis instalado e rodando na porta 6379 (ou ajuste `application.yml`)

#### Passos

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd demo
   ```

2. **Inicie o Redis** (se não estiver rodando)
   ```bash
   # Linux/Mac
   redis-server
   
   # Windows (usando Redis portable na pasta resources)
   cd src/main/resources/Redis-x64-3.0.504
   redis-server.exe
   ```

3. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```
   
   Ou compile e execute o JAR:
   ```bash
   mvn clean package
   java -jar target/produtos-0.0.1-SNAPSHOT.jar
   ```

4. **Acesse a aplicação**
   - **API REST**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **H2 Console**: http://localhost:8080/h2-console

## 📚 Documentação da API

A API está totalmente documentada com **SpringDoc OpenAPI 3.0**. Acesse a interface interativa do Swagger para:

- Visualizar todos os endpoints disponíveis
- Ver exemplos de requisições e respostas
- Testar os endpoints diretamente pelo navegador
- Consultar os modelos de dados (schemas)

**URLs de Documentação:**
- **Swagger UI (Interface Interativa)**: http://localhost:8080/swagger-ui.html
- **OpenAPI Specification (JSON)**: http://localhost:8080/v3/api-docs
- **OpenAPI Specification (YAML)**: http://localhost:8080/v3/api-docs.yaml

## 🔌 Endpoints da API

### Produtos

| Método | Endpoint | Descrição | Paginação | Cache |
|--------|----------|-----------|-----------|-------|
| `GET` | `/api/produtos` | Lista todos os produtos | ✅ | ❌ |
| `GET` | `/api/produtos/{id}` | Busca produto por ID | ❌ | ✅ Redis |
| `GET` | `/api/produtos/categoria/{categoria}` | Busca por categoria | ✅ | ✅ Redis |
| `GET` | `/api/produtos/restaurante/{restauranteId}` | Busca por restaurante | ✅ | ✅ Redis |
| `POST` | `/api/produtos` | Cria novo produto | ❌ | ❌ |
| `PUT` | `/api/produtos/{id}` | Atualiza produto | ❌ | 🔄 Atualiza |
| `DELETE` | `/api/produtos/{id}` | Remove produto | ❌ | 🗑️ Invalida |

### Parâmetros de Paginação

Todos os endpoints paginados aceitam os seguintes query parameters:

- `page` - Número da página (inicia em 0, padrão: 0)
- `size` - Quantidade de itens por página (padrão: 10)

**Exemplo:**
```bash
GET /api/produtos?page=0&size=20
GET /api/produtos/categoria/LANCHE?page=1&size=15
```

### Resposta Paginada Customizada

```json
{
  "content": [],
  "totalPages": 4,
  "totalElements": 35
}
```

**Campos:**
- `content` - Array com os produtos da página atual
- `totalPages` - Total de páginas disponíveis
- `totalElements` - Total de elementos no banco de dados

## 📝 Exemplos de Requisições

### Listar Todos os Produtos (Paginado)

```bash
curl http://localhost:8080/api/produtos?page=0&size=10
```

### Buscar Produto por ID

```bash
curl http://localhost:8080/api/produtos/1
```

### Buscar por Categoria

```bash
curl http://localhost:8080/api/produtos/categoria/LANCHE?page=0&size=10
```

### Buscar por Restaurante

```bash
curl http://localhost:8080/api/produtos/restaurante/1?page=0&size=10
```

### Criar Produto (POST)

```bash
curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Pizza Pepperoni",
    "descricao": "Massa fina, molho de tomate, mussarela e pepperoni",
    "preco": 48.90,
    "categoria": "PIZZA",
    "restauranteId": 1,
    "imagemUrl": "http://exemplo.com/pizza.jpg",
    "disponivel": true,
    "tempoPreparoMinutos": 30
  }'
```

### Atualizar Produto (PUT)

```bash
curl -X PUT http://localhost:8080/api/produtos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Pizza Pepperoni Premium",
    "descricao": "Massa fina, molho de tomate, mussarela e pepperoni importado",
    "preco": 55.90,
    "categoria": "PIZZA",
    "restauranteId": 1,
    "imagemUrl": "http://exemplo.com/pizza-premium.jpg",
    "disponivel": true,
    "tempoPreparoMinutos": 35
  }'
```

### Deletar Produto (DELETE)

```bash
curl -X DELETE http://localhost:8080/api/produtos/1
```

### Categorias Disponíveis

- `BEBIDA` - Bebidas (refrigerantes, sucos, etc.)
- `ENTRADA` - Entradas e aperitivos
- `PRATO_PRINCIPAL` - Pratos principais
- `SOBREMESA` - Sobremesas
- `LANCHE` - Lanches e sanduíches
- `PIZZA` - Pizzas
- `JAPONESA` - Comida japonesa

## 🗄️ Cache Redis

A aplicação utiliza **Redis** como provedor de cache distribuído para otimizar o desempenho das consultas.

### Caches Implementados

| Cache | Chave | Descrição | TTL |
|-------|-------|-----------|-----|
| `produtos` | ID do produto | Cache individual por produto | 10 min |
| `produtosPorCategoria` | Categoria | Cache de listagem por categoria | 10 min |
| `produtosPorRestaurante` | ID do restaurante | Cache de listagem por restaurante | 10 min |

### Estratégia de Invalidação

- **Criar produto**: Invalida caches de listagem (categoria e restaurante)
- **Atualizar produto**: Atualiza cache individual + invalida caches de listagem
- **Deletar produto**: Remove cache individual + invalida caches de listagem

### Verificar Cache Redis

```bash
# Acessar Redis CLI
docker-compose exec redis redis-cli

# Listar todas as chaves
KEYS *

# Ver valor de uma chave específica
GET produtos::1

# Limpar todo o cache
FLUSHALL

# Ver informações do Redis
INFO
```

## 🧪 Testes

O projeto possui **100% de cobertura** de testes unitários (44 testes).

### Executar Testes

```bash
# Com Docker
docker-compose exec produtos-api mvn test

# Localmente
mvn test

# Com relatório de cobertura
mvn test jacoco:report
```

### Testes Implementados

- **ProdutoServiceTest** (14 testes) - Lógica de negócio
- **ProdutoControllerTest** (17 testes) - Endpoints REST
- **GlobalExceptionHandlerTest** (3 testes) - Tratamento de exceções
- **ProdutoNotFoundExceptionTest** (3 testes) - Exceções customizadas
- **DadosIniciaisConfigTest** (3 testes) - Inicialização de dados
- **ProdutoTest** (8 testes) - Entidade e Builder pattern

**Total: 44 testes, 0 falhas, 100% de cobertura**

## ✨ Boas Práticas Implementadas

### Arquitetura e Design

- **Arquitetura em Camadas**: Separação clara entre Controller, Service, Repository, Entity e DTO
- **SOLID**: Princípios aplicados para garantir manutenção e extensibilidade
- **Records do Java 17**: DTOs imutáveis e concisos (sem Lombok)
- **Builder Pattern**: Implementação manual na entidade `Produto`
- **RESTful API**: Seguindo convenções REST (verbos HTTP, status codes, recursos)

### Performance e Escalabilidade

- **Cache Distribuído Redis**: Reduz drasticamente o número de queries ao banco
- **Paginação**: Todos os endpoints de listagem suportam paginação
- **Resposta Customizada**: Payload reduzido (apenas campos essenciais)
- **TTL Configurável**: Expiração automática de cache (10 minutos)

### Qualidade de Código

- **100% Cobertura de Testes**: 44 testes unitários cobrindo todas as classes, métodos, linhas e branches
- **Validação de Dados**: Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`)
- **Tratamento de Exceções**: `@ControllerAdvice` para capturar e formatar erros padronizados
- **Documentação Completa**: JavaDoc em todas as classes e métodos públicos
- **Swagger/OpenAPI**: Documentação interativa da API

### DevOps e Containerização

- **Docker Multi-stage Build**: Imagem otimizada (build + runtime separados)
- **Docker Compose**: Orquestração de múltiplos serviços (API + Redis)
- **Health Checks**: Garantem que Redis esteja saudável antes da API iniciar
- **Restart Policy**: Containers reiniciam automaticamente em caso de falha
- **.dockerignore**: Build otimizado (exclui arquivos desnecessários)

### Segurança e Manutenibilidade

- **Transações**: `@Transactional` para garantir consistência de dados
- **Read-Only Transactions**: Otimização para operações de leitura
- **Imutabilidade**: Records garantem DTOs imutáveis
- **Versionamento**: Git com `.gitignore` configurado
- **Documentação de Vulnerabilidades**: CVEs conhecidas documentadas no `pom.xml`

## 📄 Documentação Adicional

Para mais detalhes sobre a execução com Docker, consulte o arquivo [README-DOCKER.md](README-DOCKER.md).

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença Apache 2.0.

## 👨‍💻 Autor

Desenvolvido como projeto de demonstração de boas práticas com Spring Boot, Docker e Redis.
