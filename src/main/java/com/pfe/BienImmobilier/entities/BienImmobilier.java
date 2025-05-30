package com.pfe.BienImmobilier.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "biens_immobiliers")
public class BienImmobilier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String adresse;
    private double prix;
    private boolean disponible = false; // Valeur par défaut false

    @Enumerated(EnumType.STRING)
    private TypeTransaction typeTransaction;

    @Enumerated(EnumType.STRING)
    private ETypeLocation typeLocation;

    private Date dateAjout;
    private double surface;
    private String localisation;

    // Attributs spécifiques aux différents types de bien
    private Integer nombresChambres;      // Spécifique à Maison et Appartement
    private Integer nombresPieces;        // Spécifique à Maison et Appartement
    private Integer nombresSalledebain;   // Spécifique à Maison et Appartement
    private Boolean jardin = false;               // Utilisation de Boolean pour accepter null
    private Boolean garage = false;               // Utilisation de Boolean pour accepter null
    private Boolean climatiseur = false;          // Utilisation de Boolean pour accepter null
    private Boolean piscine = false;              // Utilisation de Boolean pour accepter null
    private Boolean balcon = false;               // Utilisation de Boolean pour accepter null
    private Boolean vueSurMer = false;            // Utilisation de Boolean pour accepter null
    private Boolean wifi = false;
    private Boolean meuble = false; // Bien meublé ou non
    private Boolean chargesIncluses = false;
    private String statutJuridique;
    private Boolean ascenseur = false;
    private Boolean interphone = false;
    private Integer nombresEtages;        // Spécifique à Appartement
    private double superficie;            // Spécifique à Terrain
    private Boolean constructible = false;        // Utilisation de Boolean pour accepter null

    private Integer views = 0;
    @Column(name = "is_verifie_admin", columnDefinition = "integer default 0")
    private Integer isVerifieAdmin = 0; // Utilisation de Boolean pour accepter null

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;  // Type de bien (Maison, Appartement, Terrain)

    // Relations
    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Utilisateur proprietaire;

    @OneToMany(mappedBy = "bienImmobilier")
    private List<Image> images;

    @ManyToOne
    @JoinColumn(name = "gouvernorat_id")
    private Gouvernorat gouvernorat;

    @Transient
    private Long gouvernoratId;

    @ManyToOne
    @JoinColumn(name = "commune_id")
    private Commune commune;

    @OneToMany(mappedBy = "bienImmobilier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avis> avis;

    @OneToMany(mappedBy = "bienImmobilier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    @ManyToMany(mappedBy = "favoris")
    private Set<Utilisateur> utilisateursFavoris = new HashSet<>();

    public String getStatutAdmin() {
        return switch (this.isVerifieAdmin) {
            case 1 -> "Approuvé";
            case -1 -> "Refusé";
            default -> "En attente";
        };
    }
}
