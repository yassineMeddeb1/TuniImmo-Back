package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.entities.EStatutReservation;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class EnumMapper {

    @Named("enumToString")
    public String enumToString(EStatutReservation statut) {
        return statut != null ? statut.name() : null;
    }

    // Si besoin de l'inverse
    @Named("stringToEnum")
    public EStatutReservation stringToEnum(String statut) {
        return statut != null ? EStatutReservation.valueOf(statut) : null;
    }
}
