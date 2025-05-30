package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImage;

    private String name;
    private String type;

    @Column(name = "IMAGE", length = 4048576)
    @Lob
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "bien_id")
    @JsonIgnore
    private BienImmobilier bienImmobilier;

    @OneToOne(mappedBy = "image")
    @JsonIgnore
    private Utilisateur utilisateur;
}
