package com.localibrary.repository;

import com.localibrary.entity.Admin;
import com.localibrary.enums.RoleAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com administradores e moderadores.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Busca admin por email
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Verifica se email já está cadastrado
     */
    boolean existsByEmail(String email);

    /**
     * Lista moderadores (RF-22)
     */
    List<Admin> findByRoleAdmin(RoleAdmin roleAdmin);
}