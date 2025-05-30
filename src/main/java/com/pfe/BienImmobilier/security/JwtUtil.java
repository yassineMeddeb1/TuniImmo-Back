package com.pfe.BienImmobilier.security;

import io.jsonwebtoken.*;
import com.pfe.BienImmobilier.entities.Utilisateur;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Méthode modifiée pour inclure les informations utilisateur
    public String generateToken(Utilisateur user, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("id", user.getId());
        claims.put("nom", user.getNom());
        claims.put("prenom", user.getPrenom());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // Email comme sujet
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Extraction de l'email (inchangé)
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraction de l'ID utilisateur
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    // Extraction du nom
    public String extractNom(String token) {
        return extractClaim(token, claims -> claims.get("nom", String.class));
    }

    // Extraction du prénom
    public String extractPrenom(String token) {
        return extractClaim(token, claims -> claims.get("prenom", String.class));
    }

    // Extraction des rôles (inchangé)
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    // Génération du token à partir du refresh token (modifié)
    public String generateTokenFromRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken, extractEmail(refreshToken))) {
            throw new RuntimeException("Refresh token invalide ou expiré");
        }

        // Créer un objet utilisateur minimal avec les infos du token
        Utilisateur user = new Utilisateur();
        user.setId(extractUserId(refreshToken));
        user.setNom(extractNom(refreshToken));
        user.setPrenom(extractPrenom(refreshToken));
        user.setEmail(extractEmail(refreshToken));

        List<String> roles = extractRoles(refreshToken);

        return generateToken(user, roles);
    }

    // Méthodes utilitaires inchangées
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, String userEmail) {
        return extractEmail(token).equals(userEmail) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}