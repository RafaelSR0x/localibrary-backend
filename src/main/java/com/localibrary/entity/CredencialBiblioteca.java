package com.localibrary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade que representa as credenciais de login de uma biblioteca.
 * Separada da entidade Biblioteca para melhor organização e segurança.
 */
@Entity
@Table(name = "tbl_credenciais_biblioteca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredencialBiblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credencial")
    private Long id;

    /**
     * Relacionamento OneToOne com Biblioteca
     */
    @OneToOne
    @JoinColumn(name = "id_biblioteca", nullable = false)
    private Biblioteca biblioteca;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}