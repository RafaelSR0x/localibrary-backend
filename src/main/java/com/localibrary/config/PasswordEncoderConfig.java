package com.localibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração do encoder de senhas.
 * Utiliza BCrypt para hash seguro de senhas.
 *
 * BCrypt:
 * - Gera hash diferente para mesma senha (salt aleatório)
 * - Resistente a rainbow tables
 * - Ajustável (strength/cost factor)
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Bean do PasswordEncoder usado em toda aplicação.
     *
     * BCrypt com strength 10 (padrão):
     * - Mais seguro que MD5/SHA
     * - Tempo de hash ~0.1s (equilibra segurança e performance)
     *
     * @return BCryptPasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}