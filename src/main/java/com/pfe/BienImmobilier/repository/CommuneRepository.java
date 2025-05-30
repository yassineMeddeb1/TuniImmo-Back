package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Commune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommuneRepository extends JpaRepository<Commune, Long> {
    List<Commune> findByGouvernoratId(Long gouvernoratId);
}
