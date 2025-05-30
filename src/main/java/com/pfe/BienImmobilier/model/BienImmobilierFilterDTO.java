package com.pfe.BienImmobilier.model;

import lombok.Data;

@Data
public class BienImmobilierFilterDTO {
    private String typeTransaction;
    private String categorie;
    private String localisation;
    private String keyword;
    private Integer prixMax;
    private Integer surfaceMin;
    private Integer nombresPieces;
    private Integer nombresChambres;
    private Integer nombresSalledebain;
    private Integer nombresEtages;
    private Long commune;
    private Long gouvernorat;

}
