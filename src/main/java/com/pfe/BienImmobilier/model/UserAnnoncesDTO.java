package com.pfe.BienImmobilier.model;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class UserAnnoncesDTO {
    private Long userId;
    private List<BienImmobilier> annonces;
}