package com.localibrary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta do login.
 * Retorna token JWT e informações básicas do usuário autenticado.
 *
 * RF-09: Login de biblioteca
 * RF-15: Login de admin/moderador
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

    /**
     * Token JWT para autenticação nas próximas requisições.
     * Deve ser enviado no header: Authorization: Bearer {token}
     */
    private String token;

    /**
     * Tipo de token (sempre "Bearer")
     */
    private String tipo = "Bearer";

    /**
     * ID do usuário (id_biblioteca ou id_admin)
     */
    private Long id;

    /**
     * Email do usuário
     */
    private String email;

    /**
     * Tipo de usuário: "BIBLIOTECA" ou "ADMIN"
     */
    private String tipoUsuario;

    /**
     * Role do usuário: "ROLE_BIBLIOTECA", "ROLE_ADMIN" ou "ROLE_MODERADOR"
     */
    private String role;

    /**
     * Nome do usuário (nomeFantasia para biblioteca, nome+sobrenome para admin)
     */
    private String nome;

    /**
     * Construtor sem o campo 'tipo' (será "Bearer" por padrão)
     */
    public TokenResponseDTO(String token, Long id, String email, String tipoUsuario, String role, String nome) {
        this.token = token;
        this.tipo = "Bearer";
        this.id = id;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.role = role;
        this.nome = nome;
    }
}