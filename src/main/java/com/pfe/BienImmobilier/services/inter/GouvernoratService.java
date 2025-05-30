package com.pfe.BienImmobilier.services;

import com.pfe.BienImmobilier.entities.Gouvernorat;

import java.util.List;
import java.util.Optional;

public interface GouvernoratService {
    List<Gouvernorat> getAllGouvernorats();
    Optional<Gouvernorat> getGouvernoratById(Long id);
}
