package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    public void envoyerEmailResetMotDePasse(String email) {
        Optional<Utilisateur> utilisateurOpt = userRepository.findByEmail(email);
        if (!utilisateurOpt.isPresent()) {
            throw new RuntimeException("Utilisateur non trouvé.");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Générer un token pour réinitialiser le mot de passe
        String token = jwtUtil.generateToken(utilisateur, List.of("reset_password"));

        // Créer un lien pour réinitialiser le mot de passe
        String resetLink = "http://localhost:4300/reset-password?token=" + token;

        // Envoyer l'e-mail
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur ce lien pour réinitialiser votre mot de passe: " + resetLink);
        mailSender.send(message);
    }

    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        String email = jwtUtil.extractEmail(token);
        Optional<Utilisateur> utilisateurOpt = userRepository.findByEmail(email);

        if (!utilisateurOpt.isPresent()) {
            throw new RuntimeException("Utilisateur non trouvé.");
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse)); // Ajouter un hash ici si nécessaire
        userRepository.save(utilisateur);
    }
}
