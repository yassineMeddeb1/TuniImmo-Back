package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.*;
import com.pfe.BienImmobilier.exceptions.NotFoundException;
import com.pfe.BienImmobilier.mapper.BienImmobilierMapper;
import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.model.BienImmobilierFilterDTO;
import com.pfe.BienImmobilier.model.NotificationDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.security.JwtUtil;
import com.pfe.BienImmobilier.services.inter.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BienImmobilierServiceImpl {

    private final BienImmobilierRepository bienImmobilierRepository;
    private final BienImmobilierMapper bienImmobilierMapper;
    private final CommuneService communeService; // Injection du service Commune
    private final JwtUtil jwtUtils;
    private final HttpServletRequest request;
    private final UserRepository utilisateurRepository;
    private final NotificationService notificationService;
    public Page<BienImmobilierDTO> searchBiens(BienImmobilierFilterDTO filter, Pageable pageable) {
        TypeTransaction typeTransaction = null;
        if (filter.getTypeTransaction() != null && !filter.getTypeTransaction().isEmpty()) {
            try {
                typeTransaction = TypeTransaction.valueOf(filter.getTypeTransaction().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Type de transaction invalide : " + filter.getTypeTransaction());
            }
        }

        Page<BienImmobilier> biens = bienImmobilierRepository.searchBiens(
                typeTransaction,
                filter.getCategorie(),
                filter.getLocalisation(),
                filter.getKeyword(),
                filter.getPrixMax(),
                filter.getSurfaceMin(),
                filter.getNombresPieces(),
                filter.getNombresChambres(),
                filter.getNombresSalledebain(),
                filter.getNombresEtages(),
                filter.getCommune(),
                filter.getGouvernorat(),
                pageable

        );

        System.out.println("Filtrage avec Commune ID: " + filter.getCommune());
        System.out.println("Filtrage avec Gouvernorat ID: " + filter.getGouvernorat());
        System.out.println("bien: " + biens);
        return biens.map(bienImmobilierMapper::toDTO);
    }
    @Transactional
    public void incrementerViews(Long bienId) {
        BienImmobilier bien = bienImmobilierRepository.findById(bienId)
                .orElseThrow(() -> new NotFoundException("Bien non trouvé"));

        bien.setViews(bien.getViews() + 1);
        bienImmobilierRepository.save(bien);
    }
    public List<BienImmobilierDTO> getTopOffers() {
        return bienImmobilierRepository.findTopOffers().stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BienImmobilierDTO> getTodayAdded() {
        return bienImmobilierRepository.findTodayAdded().stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<BienImmobilierDTO> getByCategorie(String categorie) {
        return bienImmobilierRepository.findByCategorie(categorie).stream()
                .map(bienImmobilierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public BienImmobilierDTO getBienById(Long id) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        return bienImmobilierMapper.toDTO(bien);
    }
    public List<BienImmobilierDTO> getBiensDuProprietaireConnecte() {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token JWT manquant ou invalide.");
        }

        String token = authHeader.substring(7);
        String email = jwtUtils.extractEmail(token);

        Utilisateur proprietaire = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé."));

        List<BienImmobilier> biens = bienImmobilierRepository.findByProprietaire(proprietaire);
        return biens.stream().map(bienImmobilierMapper::toDTO).collect(Collectors.toList());
    }


    public BienImmobilierDTO createBien(BienImmobilier bien) {
        // Vérifier et récupérer la commune
        Commune commune = bien.getCommune();
        if (commune == null || commune.getId() == null) {
            throw new RuntimeException("La commune est requise pour créer un bien.");
        }
        commune = communeService.getCommuneById(commune.getId());
        Gouvernorat gouvernorat = commune.getGouvernorat();

        // 🔐 Extraire le token JWT depuis l'en-tête
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        String token = authHeader.substring(7); // Remove "Bearer "

        // 🔐 Extraire l'email depuis le token
        String email = jwtUtils.extractEmail(token);

        // 🔐 Trouver l'utilisateur
        Utilisateur proprietaire = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Associer l'utilisateur en tant que propriétaire
        bien.setProprietaire(proprietaire);
        bien.setCommune(commune);
        bien.setGouvernorat(gouvernorat);

        BienImmobilier savedBien = bienImmobilierRepository.save(bien);

        List<Utilisateur> admins = utilisateurRepository.findByRoleType(RoleType.ADMIN);

        String messageNotif = String.format(
                "Nouveau bien ajouté par %s %s : %s",
                proprietaire.getPrenom(),
                proprietaire.getNom(),
                savedBien.getTitre()
        );

        NotificationDTO notif = new NotificationDTO(
                messageNotif,
                ENotificationType.NOUVEL_ANNOCE,
                savedBien.getId()
        );

        admins.forEach(admin -> notificationService.envoyerNotification(admin, notif));

        return bienImmobilierMapper.toDTO(savedBien);
    }

    public BienImmobilierDTO updateBien(Long id, BienImmobilier bienDetails) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));
        Commune commune = bienDetails.getCommune();
        if (commune == null || commune.getId() == null) {
            throw new RuntimeException("La commune est requise pour créer un bien.");
        }
        commune = communeService.getCommuneById(commune.getId());
        Gouvernorat gouvernorat = commune.getGouvernorat();

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        bien.setTitre(bienDetails.getTitre());
        bien.setDescription(bienDetails.getDescription());
        bien.setAdresse(bienDetails.getAdresse());
        bien.setPrix(bienDetails.getPrix());
        bien.setDisponible(bienDetails.isDisponible());
        bien.setTypeTransaction(bienDetails.getTypeTransaction());
        bien.setDateAjout(bienDetails.getDateAjout());
        bien.setSurface(bienDetails.getSurface());
        bien.setLocalisation(bienDetails.getLocalisation());

        bien.setCommune(commune);
        bien.setGouvernorat(gouvernorat);

        BienImmobilier updatedBien = bienImmobilierRepository.save(bien);
        return bienImmobilierMapper.toDTO(updatedBien);
    }


    public void deleteBien(Long id) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));
        bienImmobilierRepository.delete(bien);
    }
    public Page<BienImmobilierDTO> getAnnoncesAdmin(
            Integer statut,
            String categorie,
            String search,
            Pageable pageable) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        Specification<BienImmobilier> spec = Specification.where(null);

        if (statut != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isVerifieAdmin"), statut));
        }

        if (categorie != null && !categorie.equals("Tous")) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("categorie").get("nom"), categorie));
        }

        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("titre")), "%" + search.toLowerCase() + "%"));
        }

        return bienImmobilierRepository.findAll(spec, pageable)
                .map(bienImmobilierMapper::toDTO);
    }

    public void updateStatutAdmin(Long id, Integer newStatut) {
        BienImmobilier bien = bienImmobilierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce non trouvée"));
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide.");
        }
        bien.setIsVerifieAdmin(newStatut);
        bienImmobilierRepository.save(bien);
    }
}
