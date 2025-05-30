package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Commune;
import com.pfe.BienImmobilier.repository.CommuneRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommuneService {
    private final CommuneRepository communeRepository;

    public List<Commune> getCommunesByGouvernorat(Long gouvernoratId) {
        return communeRepository.findByGouvernoratId(gouvernoratId);
    }

    public Long getGouvernoratByCommune(long id) {
        Commune commune = getCommuneById(id);
        return commune.getGouvernorat().getId();
    }

    public Commune getCommuneById(Long id) {
        return communeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commune non trouvée avec l'ID : " + id));
    }
}
