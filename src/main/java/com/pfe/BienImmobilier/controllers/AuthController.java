package com.pfe.BienImmobilier.controllers;


import com.pfe.BienImmobilier.model.*;

import com.pfe.BienImmobilier.services.impl.AuthService;
import com.pfe.BienImmobilier.services.impl.PasswordResetService;
import com.pfe.BienImmobilier.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @Autowired
    private PasswordResetService passwordResetService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody SignupRequestDTO request) {
        return ResponseEntity.ok(authService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.authenticateUser(request);

        // Créer un cookie sécurisé HttpOnly avec le token JWT
        Cookie cookie = new Cookie("JWT", authResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Assurez-vous d'utiliser HTTPS en production
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // Le cookie expire dans 1 jour
        response.addCookie(cookie);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire immédiatement
        response.addHeader("Set-Cookie", "JWT=; HttpOnly; Secure; Path=/; SameSite=None; Max-Age=0");
        return ResponseEntity.ok("Déconnexion réussie");
    }

    @GetMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean success = authService.verifyCode(email, code);
        if (success) {
            return ResponseEntity.ok("Compte vérifié avec succès !");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code invalide");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String token) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token manquant");
        }

        // Extraire l'email du token JWT actuel
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));  // Extraire l'email du token actuel

        // Vérifier que le refresh token est valide pour cet utilisateur (comparaison avec l'email)
        if (!jwtUtil.validateToken(refreshToken, email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token invalide ou expiré");
        }

        // Générer un nouveau access token à partir du refresh token
        String newAccessToken = jwtUtil.generateTokenFromRefreshToken(refreshToken);

        return ResponseEntity.ok(Collections.singletonMap("token", newAccessToken));
    }
    @PostMapping("/forgot-password")
    public void requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.envoyerEmailResetMotDePasse(email);
    }
    // Réinitialiser le mot de passe
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.reinitialiserMotDePasse(request.getToken(), request.getNewPassword());
    }
}
