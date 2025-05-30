package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.model.BienImmobilierDTO;
import com.pfe.BienImmobilier.entities.Categorie;
import com.pfe.BienImmobilier.entities.Commune;
import com.pfe.BienImmobilier.entities.Gouvernorat;
import com.pfe.BienImmobilier.entities.Utilisateur;
import com.pfe.BienImmobilier.entities.Avis;
import com.pfe.BienImmobilier.entities.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface FavorisMapper {
    FavorisMapper INSTANCE = Mappers.getMapper(FavorisMapper.class);

    // Mapping de BienImmobilier vers BienImmobilierDTO
//    @Mapping(source = "categorie", target = "categorie", qualifiedByName = "mapCategorie")
//    @Mapping(source = "commune", target = "commune", qualifiedByName = "mapCommune")
//    @Mapping(source = "avis", target = "avis", qualifiedByName = "mapAvis")
    @Mapping(source = "utilisateursFavoris", target = "utilisateursFavoris", qualifiedByName = "mapUtilisateursFavoris")
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImages")
//    @Mapping(source = "gouvernorat", target = "gouvernorat", qualifiedByName = "mapGouvernorat")
//    @Mapping(source = "proprietaire", target = "proprietaire", qualifiedByName = "mapProprietaire")  // Ajout du mappage
    BienImmobilierDTO mapToModel(BienImmobilier bienImmobilier);

    List<BienImmobilierDTO> mapToModels(List<BienImmobilier> biensImmobiliers);

    // Méthode de conversion pour "Categorie" vers "String"
//    @Named("mapCategorie")
//    default String mapCategorie(Categorie categorie) {
//        return categorie != null ? categorie.getNom() : null;
//    }

    // Méthode de conversion pour "Commune" vers "String"
//    @Named("mapCommune")
//    default String mapCommune(Commune commune) {
//        return commune != null ? commune.getNom() : null;
//    }

    // Méthode de conversion pour "Utilisateur" vers "String"
    @Named("mapUtilisateur")
    default String mapUtilisateur(Utilisateur utilisateur) {
        return utilisateur != null ? utilisateur.getNom() : null;
    }

    // Méthode de conversion pour "Avis" vers "String" (par exemple, le contenu de l'avis)
//    @Named("mapAvis")
//    default List<String> mapAvis(List<Avis> avisList) {
//        return avisList != null ? avisList.stream().map(Avis::getCommentaire).collect(Collectors.toList()) : null;
//    }

    // Méthode de conversion pour "Set<Utilisateur>" vers "Set<Long>" (extraction des IDs des utilisateurs)
    @Named("mapUtilisateursFavoris")
    default Set<Long> mapUtilisateursFavoris(Set<Utilisateur> utilisateurs) {
        return utilisateurs != null ? utilisateurs.stream().map(Utilisateur::getId).collect(Collectors.toSet()) : null;
    }

    // Méthode de conversion pour "List<Image>" vers "List<String>" (par exemple, URL des images ou noms)
    @Named("mapImages")
    default List<String> mapImages(List<Image> images) {
        if (images == null) {
            return null;
        }
        return images.stream().map(Image::getName).collect(Collectors.toList());
    }
//    @Named("mapGouvernorat")
//    default String mapGouvernorat(Gouvernorat gouvernorat) {
//        return gouvernorat != null ? gouvernorat.getNom() : null;
//    }
    // Ajoutez cette méthode pour mapper un "Utilisateur" vers "String" pour le champ "proprietaire"
    @Named("mapProprietaire")
    default String mapProprietaire(Utilisateur proprietaire) {
        return proprietaire != null ? proprietaire.getNom() : null; // ou `proprietaire.getId()` si vous voulez l'ID
    }
}
