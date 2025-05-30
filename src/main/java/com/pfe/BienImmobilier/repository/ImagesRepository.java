package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesRepository extends JpaRepository<Image, Long> {
    List<Image> findByUtilisateurId(Long utilisateurId);

}
