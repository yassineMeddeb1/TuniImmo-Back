package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.mapper.FavorisMapper;
import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.inter.FavorisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FavorisServiceImpl implements FavorisService {

    private final UserRepository utilisateurRepository;
    private final BienImmobilierRepository bienImmobilierRepository;

    @Override
    public List<BienImmobilierDTO> ajouterFavori(Long utilisateurId, Long bienId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        BienImmobilier bien = bienImmobilierRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien immobilier non trouvé"));

        // Vérifier si le bien est déjà dans les favoris de l'utilisateur


        // Ajouter le bien aux favoris
        utilisateur.getFavoris().add(bien);
        utilisateurRepository.save(utilisateur);

        // Retourner la liste des favoris mise à jour
        return FavorisMapper.INSTANCE.mapToModels(utilisateur.getFavoris().stream().toList());
    }

    @Override
    public List<BienImmobilierDTO> supprimerFavori(Long utilisateurId, Long bienId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        BienImmobilier bien = bienImmobilierRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien immobilier non trouvé"));

        // Supprimer le bien des favoris
        utilisateur.getFavoris().remove(bien);
        utilisateurRepository.save(utilisateur);

        // Retourner la liste des favoris mise à jour
        return FavorisMapper.INSTANCE.mapToModels(utilisateur.getFavoris().stream().toList());
    }

    @Override
    public List<BienImmobilierDTO> obtenirFavoris(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Retourner la liste des favoris de l'utilisateur
        return FavorisMapper.INSTANCE.mapToModels(utilisateur.getFavoris().stream().toList());
    }
}
