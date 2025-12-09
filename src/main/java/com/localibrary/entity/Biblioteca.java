package com.localibrary.entity;

import com.localibrary.enums.CategoriaBiblioteca;
import com.localibrary.enums.StatusBiblioteca;
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
 * Entidade que representa uma biblioteca no sistema.
 * Cada biblioteca possui um endereço único e pode ter múltiplos livros.
 */
@Entity
@Table(name = "tbl_biblioteca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Biblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_biblioteca")
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 100)
    private String nomeFantasia;

    @Column(name = "razao_social", nullable = false, length = 100)
    private String razaoSocial;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaBiblioteca categoria;

    @Column(length = 100)
    private String site;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBiblioteca status = StatusBiblioteca.PENDENTE;

    /**
     * Relacionamento OneToOne com Endereco
     * Cada biblioteca tem exatamente um endereço
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", unique = true, nullable = false)
    private Endereco endereco;

    @Column(name = "foto_biblioteca", length = 255)
    private String fotoBiblioteca;

    /**
     * Relacionamento OneToMany com BibliotecaLivro
     * Uma biblioteca pode ter múltiplos livros
     */
    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BibliotecaLivro> livros = new ArrayList<>();

    /**
     * Relacionamento OneToMany com HorarioFuncionamento
     * Uma biblioteca pode ter múltiplos horários (um por dia da semana)
     */
    @OneToMany(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamento> horariosFuncionamento = new ArrayList<>();

    /**
     * Relacionamento OneToOne com CredencialBiblioteca
     */
    @OneToOne(mappedBy = "biblioteca", cascade = CascadeType.ALL, orphanRemoval = true)
    private CredencialBiblioteca credencial;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}