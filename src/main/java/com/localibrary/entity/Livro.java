package com.localibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa o catálogo global de livros.
 * Um livro pode estar disponível em múltiplas bibliotecas.
 */
@Entity
@Table(name = "tbl_livro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 255)
    private String autor;

    @Column(nullable = false, unique = true, length = 13)
    private String isbn;

    @Column(length = 100)
    private String editora;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(length = 255)
    private String capa;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @Column(name = "foto_autor", length = 255)
    private String fotoAutor;

    /**
     * Relacionamento OneToMany com BibliotecaLivro
     * Um livro pode estar em múltiplas bibliotecas
     */
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BibliotecaLivro> bibliotecas = new ArrayList<>();

    /**
     * Relacionamento ManyToMany com Genero (via LivroGenero)
     * Um livro pode ter múltiplos gêneros
     */
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LivroGenero> generos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}