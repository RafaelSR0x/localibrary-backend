package com.localibrary.repository;

import com.localibrary.entity.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações com gêneros literários.
 */
@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {

    /**
     * Busca gênero por nome
     */
    Optional<Genero> findByNomeGenero(String nomeGenero);

    /**
     * Verifica se gênero existe
     */
    boolean existsByNomeGenero(String nomeGenero);
}