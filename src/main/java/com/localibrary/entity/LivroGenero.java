package com.localibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidade que representa o relacionamento N:N entre LivroBase e Genero.
 * Indica quais gêneros literários pertencem a cada livro.
 */
@Entity
@Table(name = "tbl_livro_genero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(LivroGenero.LivroGeneroId.class)
public class LivroGenero {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_livro_base", nullable = false)
    private LivroBase livroBase;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_genero", nullable = false)
    private Genero genero;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Classe interna para representar a chave composta
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LivroGeneroId implements Serializable {
        private Long livroBase;
        private Long genero;
    }
}