package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.Gouvernorat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GouvernoratRepository extends JpaRepository<Gouvernorat, Long> {
}
