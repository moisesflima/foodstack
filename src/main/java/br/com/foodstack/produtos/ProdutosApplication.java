package br.com.foodstack.produtos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Classe principal para inicialização da aplicação Spring Boot.
 */
@SpringBootApplication
@EnableCaching
public class ProdutosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProdutosApplication.class, args);
    }
}
