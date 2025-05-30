package com.pfe.BienImmobilier.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ETypeLocation {
    ANNUELLE,
    MENSUELLE,
    PAR_NUIT;

    @JsonCreator
    public static ETypeLocation fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return ETypeLocation.valueOf(value.toUpperCase());
    }
}