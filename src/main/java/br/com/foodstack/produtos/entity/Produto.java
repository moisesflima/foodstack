package br.com.foodstack.produtos.entity;

import br.com.foodstack.produtos.enums.Categoria;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Produto no banco de dados.
 */
@Entity
@Table(name = "produtos")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Produto {

    /**
     * Identificador único do produto.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * Descrição detalhada do produto.
     */
    private String descricao;

    /**
     * Preço do produto.
     */
    @Column(nullable = false)
    private BigDecimal preco;

    /**
     * Categoria do produto.
     */
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    /**
     * ID do restaurante ao qual o produto pertence.
     */
    @Column(nullable = false)
    private Long restauranteId;

    /**
     * URL da imagem do produto.
     */
    private String imagemUrl;

    /**
     * Indica se o produto está disponível para venda.
     */
    private Boolean disponivel = true;

    /**
     * Tempo estimado de preparo em minutos.
     */
    private Integer tempoPreparoMinutos;

    /**
     * Data e hora de criação do registro.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Data e hora da última atualização do registro.
     */
    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    // Construtores
    public Produto() {
    }

    private Produto(Builder builder) {
        this.nome = builder.nome;
        this.descricao = builder.descricao;
        this.preco = builder.preco;
        this.categoria = builder.categoria;
        this.restauranteId = builder.restauranteId;
        this.imagemUrl = builder.imagemUrl;
        this.disponivel = builder.disponivel;
        this.tempoPreparoMinutos = builder.tempoPreparoMinutos;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nome;
        private String descricao;
        private BigDecimal preco;
        private Categoria categoria;
        private Long restauranteId;
        private String imagemUrl;
        private Boolean disponivel = true;
        private Integer tempoPreparoMinutos;

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder preco(BigDecimal preco) {
            this.preco = preco;
            return this;
        }

        public Builder categoria(Categoria categoria) {
            this.categoria = categoria;
            return this;
        }

        public Builder restauranteId(Long restauranteId) {
            this.restauranteId = restauranteId;
            return this;
        }

        public Builder imagemUrl(String imagemUrl) {
            this.imagemUrl = imagemUrl;
            return this;
        }

        public Builder disponivel(Boolean disponivel) {
            this.disponivel = disponivel;
            return this;
        }

        public Builder tempoPreparoMinutos(Integer tempoPreparoMinutos) {
            this.tempoPreparoMinutos = tempoPreparoMinutos;
            return this;
        }

        public Produto build() {
            return new Produto(this);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public Integer getTempoPreparoMinutos() {
        return tempoPreparoMinutos;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void setTempoPreparoMinutos(Integer tempoPreparoMinutos) {
        this.tempoPreparoMinutos = tempoPreparoMinutos;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
