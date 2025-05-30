package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.model.AvisDTO;
import com.pfe.BienImmobilier.model.AvisRequestDTO;
import com.pfe.BienImmobilier.entities.Avis;
import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Utilisateur;

import com.pfe.BienImmobilier.mapper.AvisMapper;
import com.pfe.BienImmobilier.repository.AvisRepository;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.UserRepository;
import com.pfe.BienImmobilier.services.inter.AvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvisServiceImpl implements AvisService {

    private final AvisRepository avisRepository;
    private final BienImmobilierRepository bienRepository;
    private final UserRepository userRepository;
    private final AvisMapper avisMapper;

    @Override
    public AvisDTO createAvis(AvisRequestDTO avisRequestDTO, String userEmail) {
        Utilisateur client = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        BienImmobilier bien = bienRepository.findById(avisRequestDTO.getBienImmobilierId())
                .orElseThrow(() -> new RuntimeException("Bien immobilier non trouvé"));

        // Vérifier si l'utilisateur a déjà posté un avis pour ce bien
        if (avisRepository.existsByAuteurAndBienImmobilier(client, bien)) {
            throw new IllegalStateException("Vous avez déjà posté un avis pour ce bien");
        }

        Avis avis = new Avis();
        avis.setNote(avisRequestDTO.getNote());
        avis.setCommentaire(avisRequestDTO.getCommentaire());
        avis.setDate(new Date());
        avis.setAuteur(client);
        avis.setBienImmobilier(bien);

        Avis savedAvis = avisRepository.save(avis);
        return avisMapper.toDto(savedAvis);
    }

    @Override
    public List<AvisDTO> getAvisByBienId(Long bienId) {
        BienImmobilier bien = bienRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien immobilier non trouvé"));

        return avisRepository.findByBienImmobilierOrderByDateDesc(bien).stream()
                .map(avisMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageRatingByBienId(Long bienId) {
        BienImmobilier bien = bienRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien immobilier non trouvé"));

        return avisRepository.getAverageRatingByBienImmobilier(bien)
                .orElse(0.0);
    }

    @Override
    public void deleteAvis(Long id, String userEmail) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis non trouvé"));

        if (!avis.getAuteur().getEmail().equals(userEmail)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cet avis");
        }

        avisRepository.delete(avis);
    }
}