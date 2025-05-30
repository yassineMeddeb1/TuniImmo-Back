package com.pfe.BienImmobilier.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageDTO { // Changer le nom pour éviter un conflit avec entities.Image

    @Schema(name = "id", description = "L'identifiant technique de l'objet image")
    private Long id;

    @Schema(name = "name", description = "Le nom de l'image")
    @NotBlank
    @Size(min = 1, max = 50) // Augmenter la taille max
    private String name;

    @Schema(name = "type", description = "Le type de l'image")
    @NotBlank
    @Size(min = 1, max = 50)
    private String type;

    @Schema(name = "picbyte", description = "Les données de l'image en binaire")
    private byte[] picbyte;
}
