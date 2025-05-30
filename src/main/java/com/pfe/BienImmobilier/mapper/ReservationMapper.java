package com.pfe.BienImmobilier.mapper;

import com.pfe.BienImmobilier.entities.Reservation;
import com.pfe.BienImmobilier.model.ReservationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BienImmobilierMapper.class, UtilisateurMapper.class, EnumMapper.class  })
public interface ReservationMapper {

    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    @Mapping(source = "bienImmobilier.id", target = "bienId")
    @Mapping(source = "utilisateur", target = "client")
    @Mapping(source = "bienImmobilier", target = "bien")
    @Mapping(source = "statut", target = "statut", qualifiedByName = "enumToString")
    ReservationDTO toDTO(Reservation reservation);

    // (Optionnel) tu peux aussi ajouter le mapping inverse si besoin
    // Reservation toEntity(ReservationDTO dto);
}
