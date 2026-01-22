# Guia de Execução com Docker

Este guia explica como executar a aplicação **Produtos API** usando Docker e Docker Compose.

## Pré-requisitos

- Docker instalado (versão 20.10 ou superior)
- Docker Compose instalado (versão 1.29 ou superior)

## Arquitetura da Solução

A aplicação é composta por dois containers:

1. **produtos-api**: API Spring Boot (porta 8080)
2. **produtos-redis**: Redis 3.0.7 para cache (porta 6379)

### Tecnologias Utilizadas

- **Spring Boot 3.5.9** com Java 17
- **Redis 3.0.7** como provedor de cache
- **H2 Database** em memória para persistência
- **Docker Multi-stage Build** para otimização da imagem

## Como Executar

### 1. Subir os containers

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Este comando irá:
- Construir a imagem da aplicação Spring Boot
- Baixar a imagem do Redis 3.0.7
- Criar uma rede bridge para comunicação entre os containers
- Iniciar o Redis e aguardar o healthcheck
- Iniciar a API Spring Boot

### 2. Verificar os logs

Para acompanhar os logs da aplicação:

```bash
# Logs de todos os serviços
docker-compose logs -f

# Logs apenas da API
docker-compose logs -f produtos-api

# Logs apenas do Redis
docker-compose logs -f redis
```

### 3. Acessar a aplicação

Após a inicialização completa, a API estará disponível em:

- **API REST**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:produtosdb`
  - Username: `sa`
  - Password: (deixar em branco)

### 4. Testar a API

Exemplos de requisições:

```bash
# Listar todos os produtos (com paginação)
curl http://localhost:8080/api/produtos

# Buscar produto por ID (resultado será cacheado no Redis)
curl http://localhost:8080/api/produtos/1

# Buscar produtos por categoria
curl http://localhost:8080/api/produtos/categoria/LANCHE

# Buscar produtos por restaurante
curl http://localhost:8080/api/produtos/restaurante/1
```

### 5. Verificar o cache no Redis

Para verificar as chaves armazenadas no Redis:

```bash
# Acessar o Redis CLI
docker exec -it produtos-redis redis-cli

# Dentro do Redis CLI, listar todas as chaves
KEYS *

# Ver o conteúdo de uma chave específica
GET "produtos::1"

# Sair do Redis CLI
exit
```

### 6. Parar os containers

```bash
# Parar os containers (mantém os dados)
docker-compose stop

# Parar e remover os containers
docker-compose down

# Parar, remover containers e volumes
docker-compose down -v
```

## Comandos Úteis

### Rebuild da aplicação

Se você fez alterações no código e quer reconstruir a imagem:

```bash
docker-compose up -d --build
```

### Reiniciar apenas a API

```bash
docker-compose restart produtos-api
```

### Ver status dos containers

```bash
docker-compose ps
```

### Acessar o shell do container da API

```bash
docker exec -it produtos-api sh
```

## Configuração do Cache Redis

A aplicação está configurada para usar Redis como provedor de cache com as seguintes características:

### Caches Implementados

1. **produtos**: Cache individual por ID do produto
   - Chave: ID do produto
   - TTL: 10 minutos

2. **produtosPorCategoria**: Cache de listagem por categoria
   - Chave: Categoria
   - TTL: 10 minutos

3. **produtosPorRestaurante**: Cache de listagem por restaurante
   - Chave: ID do restaurante
   - TTL: 10 minutos

### Estratégia de Invalidação

- **Criação de produto**: Invalida caches de listagem (categoria e restaurante)
- **Atualização de produto**: Atualiza cache individual + invalida caches de listagem
- **Exclusão de produto**: Remove do cache individual + invalida caches de listagem

### Configuração (application.yml)

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutos
  data:
    redis:
      host: redis  # Nome do serviço no Docker Compose
      port: 6379
      timeout: 2000ms
```

## Estrutura dos Arquivos Docker

### Dockerfile

- **Stage 1 (build)**: Compila a aplicação usando Maven
- **Stage 2 (runtime)**: Cria imagem leve com JRE Alpine + JAR executável

### docker-compose.yml

Define dois serviços:
- **redis**: Redis 3.0.7 com healthcheck
- **produtos-api**: API Spring Boot que depende do Redis

### .dockerignore

Exclui arquivos desnecessários do contexto de build para otimizar o processo.

## Troubleshooting

### Erro: "Connection refused" ao conectar no Redis

Verifique se o Redis está rodando e saudável:

```bash
docker-compose ps
docker-compose logs redis
```

### Aplicação não inicia

Verifique os logs para identificar o erro:

```bash
docker-compose logs produtos-api
```

### Porta 8080 ou 6379 já está em uso

Altere as portas no `docker-compose.yml`:

```yaml
ports:
  - "8081:8080"  # Muda porta externa para 8081
```

### Rebuild completo

Se houver problemas persistentes, faça um rebuild completo:

```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Diferenças em Relação ao Cache Simples

| Aspecto | Cache Simples (Anterior) | Redis (Atual) |
|---------|-------------------------|---------------|
| **Tipo** | Em memória (ConcurrentHashMap) | Redis distribuído |
| **Persistência** | Perdido ao reiniciar | Pode ser persistido |
| **Escalabilidade** | Apenas local | Compartilhado entre instâncias |
| **TTL** | Não configurado | 10 minutos |
| **Monitoramento** | Limitado | Redis CLI, métricas |
| **Produção** | Não recomendado | Recomendado |

## Próximos Passos

Para ambientes de produção, considere:

1. **Persistência do Redis**: Configurar RDB ou AOF
2. **Redis Cluster**: Para alta disponibilidade
3. **Monitoramento**: Integrar com Prometheus/Grafana
4. **Secrets**: Usar Docker Secrets ou variáveis de ambiente seguras
5. **Health Checks**: Adicionar endpoint de health na API
6. **Logs Centralizados**: Integrar com ELK Stack ou similar
