package com.pfe.BienImmobilier.repository;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.TypeTransaction;
import com.pfe.BienImmobilier.entities.Utilisateur;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BienImmobilierRepository extends JpaRepository<BienImmobilier, Long>, JpaSpecificationExecutor<BienImmobilier> {

    @Modifying
    @Query("UPDATE BienImmobilier b SET b.disponible = true WHERE b.id = :id")
    void mettreBienDisponible(@Param("id") Long id);

    List<BienImmobilier> findByProprietaire(Utilisateur proprietaire);
    @Query("SELECT b FROM BienImmobilier b WHERE " +
            "(:typeTransaction IS NULL OR b.typeTransaction = :typeTransaction) AND " +
            "(COALESCE(:categorie, '') = '' OR b.categorie.nom = :categorie) AND " +
            "(:localisation IS NULL OR b.localisation = :localisation) AND " +
            "(COALESCE(:keyword, '') = '' OR LOWER(b.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:prixMax IS NULL OR b.prix <= :prixMax) AND " +

            "(:surfaceMin IS NULL OR b.surface >= :surfaceMin) AND " +
            "(:nombresPieces IS NULL OR b.nombresPieces >= :nombresPieces) AND " +
            "(:nombresChambres IS NULL OR b.nombresChambres >= :nombresChambres) AND " +
            "(:nombresSalledebain IS NULL OR b.nombresSalledebain >= :nombresSalledebain) AND " +
            "(:nombresEtages IS NULL OR b.nombresEtages >= :nombresEtages)AND"+
            "(:commune IS NULL OR b.commune.id = :commune) AND " + // Correction ici
            "(:gouvernorat IS NULL OR b.gouvernorat.id = :gouvernorat) ") // Correction ici)

    Page<BienImmobilier> searchBiens(
            @Param("typeTransaction") TypeTransaction typeTransaction,
            @Param("categorie") String categorie,
            @Param("localisation") String localisation,
            @Param("keyword") String keyword,
            @Param("prixMax") Integer prixMax,
            @Param("surfaceMin") Integer surfaceMin,
            @Param("nombresPieces") Integer nombresPieces,
            @Param("nombresChambres") Integer nombresChambres,
            @Param("nombresSalledebain") Integer nombresSalledebain,
            @Param("nombresEtages") Integer nombresEtages,
            @Param("commune") Long communeId, // Correction: Long au lieu de Integer
            @Param("gouvernorat") Long gouvernoratId, // Correction: Long au lieu de Integer
            Pageable pageable);

    @Query("SELECT b FROM BienImmobilier b " +
            "WHERE b.disponible = true " +
            "AND b.isVerifieAdmin = 1 " +
            "AND b.nombresChambres >= 3 " +  // Exiger un minimum de chambres
            "AND (b.piscine = true OR b.jardin = true) " + // Ou bien ayant piscine ou jardin
            "ORDER BY b.dateAjout DESC, b.nombresChambres DESC, b.prix ASC Limit 5")
    List<BienImmobilier> findTopOffers();


    @Query("SELECT b FROM BienImmobilier b WHERE b.isVerifieAdmin = 1  ORDER BY b.dateAjout DESC LIMIT 4")
    List<BienImmobilier> findTodayAdded();

    @Query("SELECT b FROM BienImmobilier b WHERE b.categorie.nom = :categorie ORDER BY b.dateAjout DESC LIMIT 5")
    List<BienImmobilier> findByCategorie(@Param("categorie") String categorie);

    @Query("SELECT COUNT(b) FROM BienImmobilier b WHERE b.isVerifieAdmin = :status")
    long countByIsVerifieAdmin(@Param("status") int status);

    @Query("SELECT b.categorie.nom, COUNT(b) FROM BienImmobilier b GROUP BY b.categorie.nom")
    List<Tuple> countPropertiesByCategory();

    @Query("SELECT COALESCE(SUM(b.views), 0) FROM BienImmobilier b")
    long sumAllViews();
}