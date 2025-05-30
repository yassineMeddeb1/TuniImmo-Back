    package com.pfe.BienImmobilier.mapper;
    
    import com.pfe.BienImmobilier.entities.*;
    import com.pfe.BienImmobilier.model.BienImmobilierDTO;
    import org.mapstruct.*;
    
    import java.util.List;
    import java.util.Set;
    import java.util.stream.Collectors;
    
    @Mapper(componentModel = "spring")
    public interface BienImmobilierMapper {
    
        // Mapping des propriétés simples
//        @Mapping(source = "categorie", target = "categorie", qualifiedByName = "mapCategorie")
//        @Mapping(source = "commune", target = "commune", qualifiedByName = "mapCommune")
//        @Mapping(source = "proprietaire", target = "proprietaire", qualifiedByName = "mapProprietaire")
        @Mapping(source = "utilisateursFavoris", target = "utilisateursFavoris", qualifiedByName = "mapFavoris")
//        @Mapping(source = "avis", target = "avis", qualifiedByName = "mapAvis")
        @Mapping(source = "images", target = "images", qualifiedByName = "mapImages")
//        @Mapping(source = "gouvernorat", target = "gouvernorat", qualifiedByName = "mapGouvernorat")
        @Mapping(target = "statutAdmin", expression = "java(bienImmobilier.getStatutAdmin())")

        BienImmobilierDTO toDTO(BienImmobilier bienImmobilier);
    
        List<BienImmobilierDTO> toDtoList(List<BienImmobilier> biens);
    
        // Méthodes de conversion personnalisées
    
        @Named("mapFavoris")
        default Set<Long> mapFavoris(Set<Utilisateur> utilisateurs) {
            if (utilisateurs == null) {
                return null;
            }
            return utilisateurs.stream().map(Utilisateur::getId).collect(Collectors.toSet());
        }
    
//        @Named("mapAvis")
//        default List<String> mapAvis(List<Avis> avis) {
//            if (avis == null) {
//                return null;
//            }
//            return avis.stream().map(Avis::getCommentaire).collect(Collectors.toList()); // Remplacer getContenu par getTexte
//        }
    
//        @Named("mapCategorie")
//        default String mapCategorie(Categorie categorie) {
//            return categorie != null ? categorie.getNom() : null;
//        }
//
//        @Named("mapCommune")
//        default Long mapCommune(Commune commune) {
//            return commune != null ? commune.getId() : null;
//        }
//
    
//        @Named("mapGouvernorat")
//        default Long mapGouvernorat(Gouvernorat gouvernorat) {
//            return gouvernorat != null ? gouvernorat.getId() : null;
//        }
    
    
//        @Named("mapProprietaire")
//        default String mapProprietaire(Utilisateur proprietaire) {
//            return proprietaire != null ? proprietaire.getNom() : null;  // Changez "getNom" en fonction de l'attribut correct
//        }
        @Named("mapImages")
        default List<String> mapImages(List<Image> images) {
            if (images == null) {
                return null;
            }
            return images.stream().map(Image::getName).collect(Collectors.toList());
        }
    
    }
