package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.Commune;
import com.pfe.BienImmobilier.entities.Gouvernorat;
import com.pfe.BienImmobilier.repository.CommuneRepository;
import com.pfe.BienImmobilier.repository.GouvernoratRepository;
import com.pfe.BienImmobilier.services.GouvernoratService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GouvernoratServiceImpl implements GouvernoratService {

    private final GouvernoratRepository gouvernoratRepository;
    private final CommuneRepository communeRepository;

    @Override
    public List<Gouvernorat> getAllGouvernorats() {
        return gouvernoratRepository.findAll();
    }

    @Override
    public Optional<Gouvernorat> getGouvernoratById(Long id) {
        return gouvernoratRepository.findById(id);
    }


}
