package com.busbook.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilitaire pour la génération et validation des tokens JWT.
 * Le token contient l'email (subject) et optionnellement le rôle de l'utilisateur (claim "role").
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /** Construit la clé de signature à partir du secret configuré */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Génère un token JWT signé pour l'email donné.
     *
     * @param email l'email de l'utilisateur (subject du token)
     * @return le token JWT en chaîne de caractères
     */
    public String generateToken(String email) {
        return generateToken(email, null);
    }

    /**
     * Génère un token JWT signé contenant l'email et le rôle.
     *
     * @param email l'email de l'utilisateur (subject du token)
     * @param role  le rôle de l'utilisateur (USER ou ADMIN)
     * @return le token JWT en chaîne de caractères
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey());

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    /**
     * Extrait l'email (subject) depuis un token JWT.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrait le rôle depuis un token JWT.
     *
     * @return le rôle (ex: "ADMIN" ou "USER"), ou null si absent
     */
    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    /**
     * Vérifie si le token est valide (signature correcte et non expiré).
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /** Parse et retourne les claims du token */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
