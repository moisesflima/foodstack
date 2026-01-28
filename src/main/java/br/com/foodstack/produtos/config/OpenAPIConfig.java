package br.com.foodstack.produtos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do SpringDoc OpenAPI para documentação da API.
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Produtos - FoodStack")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de produtos de restaurantes estilo iFood. " +
                                "Permite criar, listar, atualizar e deletar produtos, com suporte a paginação, " +
                                "cache distribuído com Redis e filtros por categoria e restaurante.")
                        .contact(new Contact()
                                .name("FoodStack Team")
                                .email("contato@foodstack.com.br")
                                .url("https://foodstack.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.foodstack.com.br")
                                .description("Servidor de Produção")
                ));
    }
}
