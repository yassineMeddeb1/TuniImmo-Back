package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Role;
import com.pfe.BienImmobilier.entities.RoleType;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.model.*;

import com.pfe.BienImmobilier.repository.RoleRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    // Pour stocker temporairement les codes de vérification (à remplacer par Redis ou DB en prod)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public AuthResponseDTO registerUser(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setRegion(request.getRegion());
        user.setAdresse(request.getAdresse());
        user.setTelephone(request.getTelephone());
        user.setEmail(request.getEmail());
        user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        user.setEnabled(false); // L'utilisateur n'est pas encore activé

        // Récupération des rôles fournis dans la requête
        List<Role> roles = request.getRoles().stream()
                .map(roleType -> roleRepository.findByRoleType(roleType)
                        .orElseThrow(() -> new RuntimeException("Rôle non trouvé : " + roleType)))
                .collect(Collectors.toList());

        // Ajout automatique du rôle VISITEUR
        Role visiteurRole = roleRepository.findByRoleType(RoleType.VISITEUR)
                .orElseThrow(() -> new RuntimeException("Rôle VISITEUR non trouvé"));
        if (!roles.contains(visiteurRole)) {
            roles.add(visiteurRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        // Générer le code de vérification et l'envoyer par mail
        String verificationCode = generateVerificationCode();
        verificationCodes.put(user.getEmail(), verificationCode);
        sendVerificationEmail(user.getEmail(), verificationCode);

        // Générer le token
        String token = jwtUtil.generateToken(user,
                user.getRoles().stream().map(r -> r.getRoleType().name()).toList());

        return new AuthResponseDTO(token, user.getEmail(), user.getNom(), user.getPrenom(), user.getRoles());
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Code de vérification - Votre inscription");
            helper.setText("Bonjour,\n\nVoici votre code de vérification : " + code + "\n\nMerci.", false);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'e-mail", e);
        }
    }

    public boolean verifyCode(String email, String code) {
        String savedCode = verificationCodes.get(email);
        if (savedCode != null && savedCode.equals(code)) {
            Utilisateur user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            user.setEnabled(true);
            userRepository.save(user);
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    public AuthResponseDTO authenticateUser(LoginRequestDTO request) {
        Utilisateur user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // Vérifier si l'utilisateur a validé son email
        if (!user.isEnabled()) {
            throw new RuntimeException("Veuillez vérifier votre email avant de vous connecter.");
        }

        String token = jwtUtil.generateToken(user,
                user.getRoles().stream().map(r -> r.getRoleType().name()).toList());

        return new AuthResponseDTO(token, user.getEmail(), user.getNom(), user.getPrenom(), user.getRoles());
    }
}
