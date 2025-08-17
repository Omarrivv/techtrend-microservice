package com.techtrend.authentication.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para manejo de JWT (JSON Web Tokens)
 * 
 * Proporciona funcionalidades para generar y validar tokens de autenticación
 * 
 * @author TechTrend Team
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Genera un token JWT para un usuario
     * 
     * @param email Email del usuario
     * @param role Rol del usuario
     * @return Token JWT generado
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, email);
    }

    /**
     * Crea un token JWT con claims específicos
     * 
     * @param claims Claims del token
     * @param subject Subject del token (email del usuario)
     * @return Token JWT generado
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el email del usuario del token
     * 
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     * 
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     * 
     * @param token Token JWT
     * @param claimsResolver Función para resolver el claim
     * @return Valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     * 
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma para el token
     * 
     * @return Clave de firma
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Verifica si el token ha expirado
     * 
     * @param token Token JWT
     * @return true si ha expirado, false en caso contrario
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Valida un token JWT
     * 
     * @param token Token JWT a validar
     * @param email Email del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return (email.equals(extractedEmail) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el rol del usuario del token
     * 
     * @param token Token JWT
     * @return Rol del usuario
     */
    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica si un token es válido sin verificar el email
     * 
     * @param token Token JWT
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
