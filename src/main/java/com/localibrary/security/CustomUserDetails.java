package com.localibrary.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementação customizada de UserDetails do Spring Security.
 * Representa um usuário autenticado (Biblioteca ou Admin).
 *
 * Contém informações necessárias para autenticação e autorização.
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String senha;
    private final String role;
    private final String tipo; // "BIBLIOTECA" ou "ADMIN"
    private final boolean ativo;

    /**
     * Construtor completo
     *
     * @param id ID do usuário (id_biblioteca ou id_admin)
     * @param email Email do usuário
     * @param senha Senha hash (BCrypt)
     * @param role Role do usuário (ROLE_BIBLIOTECA, ROLE_ADMIN, ROLE_MODERADOR)
     * @param tipo Tipo de usuário ("BIBLIOTECA" ou "ADMIN")
     * @param ativo Se o usuário está ativo
     */
    public CustomUserDetails(Long id, String email, String senha, String role, String tipo, boolean ativo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    /**
     * Retorna as authorities (roles) do usuário.
     * Usado pelo Spring Security para autorização.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Conta nunca expira (controle manual via status/ativo)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Conta nunca é bloqueada (controle manual via status/ativo)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credenciais nunca expiram (controle via JWT expiration)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Verifica se usuário está ativo
     * Bibliotecas: status == ATIVO
     * Admins: sempre true (não tem campo ativo na tabela)
     */
    @Override
    public boolean isEnabled() {
        return ativo;
    }

    // Getters adicionais

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getTipo() {
        return tipo;
    }
}