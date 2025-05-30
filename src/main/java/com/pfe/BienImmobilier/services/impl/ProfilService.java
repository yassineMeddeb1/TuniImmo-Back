package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.mapper.UtilisateurMapper;
import com.pfe.BienImmobilier.model.UpdateProfileRequest;
import com.pfe.BienImmobilier.model.UtilisateurResponse;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfilService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository utilisateurRepository;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UtilisateurResponse getUtilisateurDepuisToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT manquant ou invalide.");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        return utilisateurMapper.toResponse(utilisateur);
    }

    public UtilisateurResponse modifierProfil(UpdateProfileRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String email = jwtUtil.extractEmail(token);

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        // Vérification du mot de passe actuel si on veut changer de mot de passe
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), utilisateur.getMotDePasse())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mot de passe actuel incorrect");
            }
            utilisateur.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setRegion(request.getRegion());
        utilisateur.setTelephone(request.getTelephone());

        Utilisateur updated = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toResponse(updated);
    }
}
