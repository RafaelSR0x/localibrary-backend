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
 * Entidade que representa o relacionamento N:N entre Biblioteca e LivroBase.
 * Indica quais livros estão disponíveis em cada biblioteca e em que quantidade.
 */
@Entity
@Table(name = "tbl_biblioteca_livro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BibliotecaLivro.BibliotecaLivroId.class)
public class BibliotecaLivro {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_biblioteca", nullable = false)
    private Biblioteca biblioteca;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_livro_base", nullable = false)
    private LivroBase livroBase;

    @Column(nullable = false)
    private Integer quantidade = 1;

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
    public static class BibliotecaLivroId implements Serializable {
        private Long biblioteca;
        private Long livroBase;
    }
}