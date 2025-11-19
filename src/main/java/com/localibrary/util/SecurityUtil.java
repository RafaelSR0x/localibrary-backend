package com.localibrary.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Busca o ID do usuário (Biblioteca ou Admin) autenticado no token JWT.
     */
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuário não autenticado.");
        }

        // Na Sprint 1 (JwtAuthenticationFilter), definimos o ID como o "Principal"
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        // Se o principal não for um Long, algo está errado (ex: 'anonymousUser')
        throw new SecurityException("Contexto de autenticação inválido.");
    }

    /**
     * RN-01: Verifica se o ID do token é o mesmo ID do recurso na URL.
     * Se não for, bloqueia com um erro 403 Forbidden.
     */
    public void checkHasPermission(Long resourceBibliotecaId) {
        Long authenticatedId = getAuthenticatedUserId();

        if (!authenticatedId.equals(resourceBibliotecaId)) {
            throw new AccessDeniedException(
                    "Acesso negado. Você não tem permissão para gerenciar este recurso."
            );
        }
    }
}