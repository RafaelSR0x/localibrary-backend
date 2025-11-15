package com.localibrary.security;

import com.localibrary.entity.Admin;
import com.localibrary.entity.Biblioteca;
import com.localibrary.entity.CredencialBiblioteca;
import com.localibrary.enums.StatusBiblioteca;
import com.localibrary.exception.UnauthorizedException;
import com.localibrary.repository.AdminRepository;
import com.localibrary.repository.CredencialBibliotecaRepository;
import com.localibrary.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço customizado para carregar detalhes do usuário.
 * Implementa UserDetailsService do Spring Security.
 *
 * Busca usuário por email em duas tabelas:
 * 1. tbl_credenciais_biblioteca (bibliotecas)
 * 2. tbl_admin (admins e moderadores)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CredencialBibliotecaRepository credencialRepository;

    @Autowired
    private AdminRepository adminRepository;

    /**
     * Carrega usuário por email (username).
     * Chamado automaticamente pelo Spring Security durante login.
     *
     * Busca primeiro em credenciais de biblioteca, depois em admin.
     *
     * @param email Email do usuário
     * @return UserDetails com dados do usuário
     * @throws UsernameNotFoundException se usuário não encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Tentar buscar em credenciais de biblioteca
        CredencialBiblioteca credencial = credencialRepository.findByEmail(email).orElse(null);

        if (credencial != null) {
            return createUserDetailsFromBiblioteca(credencial);
        }

        // 2. Tentar buscar em admin
        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin != null) {
            return createUserDetailsFromAdmin(admin);
        }

        // 3. Usuário não encontrado
        throw new UsernameNotFoundException("Usuário não encontrado com email: " + email);
    }

    /**
     * Cria CustomUserDetails a partir de CredencialBiblioteca.
     *
     * Regras:
     * - Role: ROLE_BIBLIOTECA
     * - Tipo: BIBLIOTECA
     * - Ativo: biblioteca.status == ATIVO
     *
     * @param credencial Credencial da biblioteca
     * @return CustomUserDetails
     */
    private CustomUserDetails createUserDetailsFromBiblioteca(CredencialBiblioteca credencial) {
        Biblioteca biblioteca = credencial.getBiblioteca();

        // Verificar se biblioteca está ativa (RN-02)
        boolean ativo = biblioteca.getStatus() == StatusBiblioteca.ATIVO;

        return new CustomUserDetails(
                biblioteca.getId(),
                credencial.getEmail(),
                credencial.getSenha(),
                Constants.ROLE_BIBLIOTECA,
                "BIBLIOTECA",
                ativo
        );
    }

    /**
     * Cria CustomUserDetails a partir de Admin.
     *
     * Regras:
     * - Role: ROLE_ADMIN ou ROLE_MODERADOR (baseado em roleAdmin)
     * - Tipo: ADMIN
     * - Ativo: sempre true (admins não têm campo "ativo")
     *
     * @param admin Admin ou moderador
     * @return CustomUserDetails
     */
    private CustomUserDetails createUserDetailsFromAdmin(Admin admin) {

        // Determinar role baseado no roleAdmin
        String role = admin.getRoleAdmin().name().equals("ADMIN")
                ? Constants.ROLE_ADMIN
                : Constants.ROLE_MODERADOR;

        return new CustomUserDetails(
                admin.getId(),
                admin.getEmail(),
                admin.getSenha(),
                role,
                "ADMIN",
                true // Admins sempre ativos
        );
    }

    /**
     * Carrega usuário por ID (útil para validações RN-01).
     * Verifica se ID no token corresponde ao recurso sendo acessado.
     *
     * @param id ID do usuário
     * @param tipo Tipo do usuário (BIBLIOTECA ou ADMIN)
     * @return CustomUserDetails
     * @throws UnauthorizedException se usuário não encontrado
     */
    public CustomUserDetails loadUserById(Long id, String tipo) {

        if ("BIBLIOTECA".equals(tipo)) {
            CredencialBiblioteca credencial = credencialRepository.findByBibliotecaId(id)
                    .orElseThrow(() -> new UnauthorizedException("Biblioteca não encontrada"));

            return createUserDetailsFromBiblioteca(credencial);

        } else if ("ADMIN".equals(tipo)) {
            Admin admin = adminRepository.findById(id)
                    .orElseThrow(() -> new UnauthorizedException("Admin não encontrado"));

            return createUserDetailsFromAdmin(admin);
        }

        throw new UnauthorizedException("Tipo de usuário inválido");
    }
}