package com.localibrary.dto.response;

import com.localibrary.entity.Admin;
import com.localibrary.enums.RoleAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Admin/Moderador.
 * Não inclui senha (segurança).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private RoleAdmin roleAdmin;

    /**
     * Construtor a partir da entidade Admin
     */
    public AdminResponseDTO(Admin admin) {
        this.id = admin.getId();
        this.nome = admin.getNome();
        this.sobrenome = admin.getSobrenome();
        this.email = admin.getEmail();
        this.roleAdmin = admin.getRoleAdmin();
    }

    /**
     * Retorna nome completo
     */
    public String getNomeCompleto() {
        return nome + " " + sobrenome;
    }
}