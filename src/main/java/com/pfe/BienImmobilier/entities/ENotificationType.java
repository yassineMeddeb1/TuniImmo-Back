package com.pfe.BienImmobilier.entities;


public enum ENotificationType {
    NOUVELLE_RESERVATION("nouvelle-reservation", "Nouvelle réservation"),
    NOUVEL_ANNOCE("Nouvelle-annonce","Nouvelle Annonce"),
    RESERVATION_CONFIRMEE("reservation-confirmee", "Réservation confirmée"),
    RESERVATION_ANNULEE("reservation-annulee", "Réservation annulée"),
    RESERVATION_TERMINEE("reservation-terminee", "Réservation terminée"),
    PAIEMENT_EFFECTUE("paiement-effectue", "Paiement effectué"),
    AVIS_RECU("avis-recu", "Avis reçu"),
    MESSAGE_NON_LU("message-non-lu", "Nouveau message");

    private final String code;
    private final String libelle;

    ENotificationType(String code, String libelle) {
        this.code = code;
        this.libelle = libelle;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }
}