package com.localibrary.security;

import com.localibrary.exception.UnauthorizedException;
import com.localibrary.util.Constants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

/**
 * Provedor de tokens JWT.
 * Responsável por gerar, validar e extrair informações de tokens JWT.
 *
 * JWT (JSON Web Token):
 * - Header: algoritmo e tipo
 * - Payload: claims (dados do usuário)
 * - Signature: assinatura para garantir integridade
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Gera token JWT para um usuário autenticado.
     *
     * Claims incluídos:
     * - sub: email do usuário
     * - id: ID do usuário (id_biblioteca ou id_admin)
     * - role: ROLE_BIBLIOTECA, ROLE_ADMIN ou ROLE_MODERADOR
     * - tipo: BIBLIOTECA ou ADMIN
     * - iat: timestamp de criação
     * - exp: timestamp de expiração
     *
     * @param userDetails Detalhes do usuário autenticado
     * @return Token JWT
     */
    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userDetails.getEmail())
                .claim(Constants.JWT_CLAIM_ID, userDetails.getId())
                .claim(Constants.JWT_CLAIM_ROLE, userDetails.getRole())
                .claim(Constants.JWT_CLAIM_TIPO, userDetails.getTipo())
                .claim(Constants.JWT_CLAIM_EMAIL, userDetails.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida um token JWT.
     *
     * Verifica:
     * - Assinatura válida
     * - Token não expirado
     * - Claims obrigatórios presentes
     *
     * @param token Token JWT
     * @return true se válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (SecurityException ex) {
            logger.error("Assinatura JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string vazia: {}", ex.getMessage());
        }

        return false;
    }

    /**
     * Extrai o email (subject) do token JWT.
     *
     * @param token Token JWT
     * @return Email do usuário
     */
    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Extrai o ID do usuário do token JWT.
     *
     * @param token Token JWT
     * @return ID do usuário
     */
    public Long getIdFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get(Constants.JWT_CLAIM_ID, Long.class);
    }

    /**
     * Extrai a role do usuário do token JWT.
     *
     * @param token Token JWT
     * @return Role (ROLE_BIBLIOTECA, ROLE_ADMIN, ROLE_MODERADOR)
     */
    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get(Constants.JWT_CLAIM_ROLE, String.class);
    }

    /**
     * Extrai o tipo do usuário do token JWT.
     *
     * @param token Token JWT
     * @return Tipo (BIBLIOTECA ou ADMIN)
     */
    public String getTipoFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get(Constants.JWT_CLAIM_TIPO, String.class);
    }

    /**
     * Cria objeto Authentication a partir do token JWT.
     * Usado pelo Spring Security para autenticação.
     *
     * @param token Token JWT
     * @return Authentication com dados do usuário
     */
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        String role = getRoleFromToken(token);

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.singletonList(authority)
        );
    }

    /**
     * Extrai todos os claims do token.
     * Útil para debug e logging.
     *
     * @param token Token JWT
     * @return Claims
     */
    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}