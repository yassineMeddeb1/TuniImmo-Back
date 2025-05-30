package com.pfe.BienImmobilier.entities;

public enum AbonnementType {
    GRATUIT(5, 29.99),
    STANDARD(15, 59.99),
    PREMIUM(50, 99.99);

    private final int maxAnnonces;
    private final double prix;

    AbonnementType(int maxAnnonces, double prix) {
        this.maxAnnonces = maxAnnonces;
        this.prix = prix;
    }

    public int getMaxAnnonces() {
        return maxAnnonces;
    }

    public double getPrix() {
        return prix;
    }
}