package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String region;
    private String adresse;

    @Column(nullable = false)
    private String motDePasse;
    private String telephone;
    private boolean enabled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "idImage")
    private Image image;


    // Relation @ManyToMany pour les rôles, avec un joinTable qui indique les clés étrangères
    @ManyToMany(fetch = FetchType.EAGER)  // Utilisation de FetchType.EAGER pour charger les rôles en même temps que l'utilisateur
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
    // Relation @OneToMany avec l'entité BienImmobilier, le propriétaire de chaque bien
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BienImmobilier> biensImmobiliers;

    // Relation @ManyToMany avec l'entité BienImmobilier pour gérer les favoris
    // Chaque utilisateur peut ajouter plusieurs biens en favoris
    @ManyToMany
    @JoinTable(
            name = "favoris_utilisateurs",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "bien_immobilier_id")
    )
    private Set<BienImmobilier> favoris = new HashSet<>();

    // Relation @OneToMany avec l'entité Reservation, chaque utilisateur a des réservations
    // La relation est bidirectionnelle, donc chaque réservation doit avoir un champ utilisateur (avec @ManyToOne)
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Reservation> reservations;

}
