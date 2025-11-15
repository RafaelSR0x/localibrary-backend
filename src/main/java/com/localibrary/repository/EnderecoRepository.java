package com.localibrary.repository;

import com.localibrary.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações de banco de dados com a entidade Endereco.
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}