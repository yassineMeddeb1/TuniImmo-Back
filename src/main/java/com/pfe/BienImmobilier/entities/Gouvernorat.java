package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "gouvernorats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gouvernorat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "gouvernorat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Commune> communes;
}
