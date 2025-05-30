package com.pfe.BienImmobilier.model;

import lombok.Data;
import java.util.List;

@Data
public class AbonnementResponseDTO {
    private AbonnementDTO currentAbonnement;
    private List<AbonnementOptionDTO> availableOptions;
}

@Data
class AbonnementOptionDTO {
    private String type;
    private Double prix;
    private Integer annoncesAutorisees;
    private List<String> features;
    private Boolean popular;
}